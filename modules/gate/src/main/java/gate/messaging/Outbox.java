package gate.messaging;

import gate.annotation.Current;
import gate.catalog.MailCatalog;
import gate.entity.App;
import gate.entity.Mail;
import gate.type.mime.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Properties;

@ApplicationScoped
class Outbox
{
	private static final Duration DEFAULT_RETRY_INTERVAL = Duration.ofHours(1);
	private static final int DEFAULT_MAX_ATTEMPTS = Integer.MAX_VALUE;

	@Inject
	@Current
	App app;

	@Inject
	Logger logger;

	@Inject
	OutboxConfig config;

	@Inject
	MailCatalog mailCatalog;

	private Properties properties;
	private Session session;

	@PostConstruct
	void init()
	{
		properties = new Properties();
		properties.put("mail.smtp.auth", Boolean.toString(config.username().isPresent() || config.password().isPresent()));
		config.host().ifPresent(host -> properties.put("mail.smtp.host", host));
		properties.put("mail.smtp.port", Integer.toString(config.port().orElse(25)));
		properties.put("mail.smtp.socketFactory.port", Integer.toString(config.port().orElse(25)));

		config.timeout().ifPresent(timeout ->
		{
			properties.put("mail.smtp.timeout", timeout);
			properties.put("mail.smtp.connectiontimeout", timeout);
		});

		if (config.tls().orElse(false))
			properties.put("mail.smtp.starttls.enable", "true");

		if (config.ssl().orElse(false))
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		Authenticator authenticator = new Authenticator()
		{
			@Override
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(config.username().orElse(null), config.password().orElse(null));
			}
		};

		session = Session.getInstance(properties, authenticator);
	}

	OutboxConfig getConfig() {return config;}


	void dispatch()
	{
		if (config.host().isEmpty())
			return;

		try
		{
			mailCatalog.expire(app, config.maxAttempts().orElse(DEFAULT_MAX_ATTEMPTS));
			for (Mail mail : mailCatalog.search(app, config.maxAttempts().orElse(DEFAULT_MAX_ATTEMPTS),
					config.retryInterval().orElse(DEFAULT_RETRY_INTERVAL)))
			{
				try
				{
					send(mail.getSender(), mail.getReceiver(), mail.getMessage());
					mailCatalog.delete(mail);
				} catch (RuntimeException ex)
				{
					logger.warn(ex.getMessage(), ex);
					mail.setAttempts(mail.getAttempts() + 1);
					mailCatalog.update(mail);
				}
			}
		} catch (RuntimeException ex)
		{
			logger.error(ex.getMessage(), ex);
		}
	}


	void send(String sender, String receiver, MimeMail<?> mail) throws MessageException
	{
		if (sender == null)
			throw new MessageException("Tentativa de enviar mensagem sem especificar o remetente.");
		if (receiver == null)
			throw new MessageException("Tentativa de enviar mensagem sem especificar o destinatário.");
		if (mail == null)
			throw new MessageException("Tentativa de enviar mensagem sem especificar seu conteúdo.");

		try
		{
			config.host().orElseThrow(() -> new MessageException("Outbound mail host not configured"));

			MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setFrom(sender);
			mimeMessage.setSubject(mail.getSubject());
			mimeMessage.setSentDate(new java.util.Date());

			if (mail.getPriority() == MimeMail.Priority.LOW)
				mimeMessage.setHeader("X-Priority", "5");
			else if (mail.getPriority() == MimeMail.Priority.HIGH)
				mimeMessage.setHeader("X-Priority", "1");

			for (var entry : mail.getHeaders().entrySet())
				for (String value : entry.getValue())
					mimeMessage.addHeader(entry.getKey(), value);

			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));

			if (mail.getContent() instanceof MimeText mimeText)
				mimeMessage.setText(mimeText.getText(), mimeText.getCharset(), mimeText.getContentType().getSubtype());
			else if (mail.getContent() instanceof MimeDataFile mimeDataFile)
			{
				mimeMessage.setDisposition("Attachment");
				mimeMessage.setFileName(mimeDataFile.getName());
				mimeMessage.setContent(mimeDataFile.getData(), "application/octet-stream");
			} else if (mail.getContent() instanceof MimeList mimeList)
				mimeMessage.setContent(getMultipart(mimeList));

			mimeMessage.saveChanges();
			try (Transport transport = session.getTransport("smtp"))
			{
				if (!transport.isConnected())
					transport.connect();
				transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
			}
		} catch (MessagingException ex)
		{
			throw new MessageException("Error trying to send mail message", ex);
		}
	}

	private MimeMultipart getMultipart(MimeList data) throws MessagingException
	{
		MimeMultipart mimeMultipart = new MimeMultipart();
		mimeMultipart.setSubType(data.getContentType().getType());

		for (Mime mime : data)
		{
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			if (mime instanceof MimeText mimeText)
				mimeBodyPart.setText(mimeText.getText(), mimeText.getCharset(), mimeText.getContentType().getSubtype());
			else if (mime instanceof MimeDataFile mimeDataFile)
			{
				mimeBodyPart.setDisposition("Attachment");
				mimeBodyPart.setFileName(mimeDataFile.getName());
				mimeBodyPart.setContent(mimeDataFile.getData(), "application/octet-stream");
			} else if (mime instanceof MimeList mimeList)
				mimeBodyPart.setContent(getMultipart(mimeList));

			mimeMultipart.addBodyPart(mimeBodyPart);
		}

		return mimeMultipart;
	}
}