package gate.messaging;

import gate.annotation.Current;
import gate.entity.App;
import gate.entity.Mail;
import gate.entity.Server;
import gate.error.AppException;
import gate.type.mime.Mime;
import gate.type.mime.MimeDataFile;
import gate.type.mime.MimeList;
import gate.type.mime.MimeMail;
import gate.type.mime.MimeText;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.servlet.ServletContextListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;

@ApplicationScoped
public class Messenger implements ServletContextListener
{

	@Inject
	@Current
	private App app;

	@Inject
	MailControl control;

	@Inject
	private Logger logger;

	public void dispatch()
	{
		if (control.isEnabled())
		{
			try
			{
				control.expire(app);
				for (Mail mail : control.search(app))
				{
					try
					{
						send(mail.getSender(), mail.getReceiver(), mail.getMessage());
						control.delete(mail);
					} catch (AppException | RuntimeException ex)
					{
						logger.warn(ex.getMessage(), ex);
						mail.setAttempts(mail.getAttempts() + 1);
						control.update(mail);
					}
				}
			} catch (AppException | RuntimeException ex)
			{
				logger.error(ex.getMessage(), ex);
			}
		}
	}

	public boolean isEnabled()
	{
		return control.isEnabled();
	}

	public void post(String sender,
			String receiver,
			MimeMail<?> message)
			throws MessageException
	{
		try
		{
			if (sender == null)
				throw new MessageException("Tentativa de enviar mensagem sem especificar o remetente.");
			if (receiver == null)
				throw new MessageException("Tentativa de enviar mensagem sem especificar o destinatário.");
			if (message == null)
				throw new MessageException("Tentativa de enviar mensagem sem especificar seu conteúdo.");

			Mail mail = new Mail();
			mail.setAttempts(0);
			mail.setSender(sender);
			mail.setApp(app.getId());
			mail.setMessage(message);
			mail.setReceiver(receiver);
			mail.setDate(LocalDateTime.now());
			mail.setExpiration(mail.getDate().plusDays(1));
			control.insert(mail);
		} catch (AppException ex)
		{
			throw new MessageException(ex.getMessage());
		}
	}

	public void post(String receiver, MimeMail<?> mail) throws MessageException
	{
		var server = control.server().orElseThrow(() -> new MessageException("SMPT server not configured"));
		post(server.getUsername(), receiver, mail);
	}

	public List<Mail> search() throws MessageException
	{
		return control.search();
	}

	private void send(String sender, String receiver, MimeMail<?> mail) throws MessageException
	{
		try
		{
			var server = control.server().orElseThrow(() -> new MessageException("SMPT server not configured"));
			send(server, sender, receiver, mail);
		} catch (MessagingException ex)
		{
			throw new MessageException("Error trying to send mail message", ex);
		}
	}

	public void send(Server server, String sender, String receiver, MimeMail<?> mail) throws MessagingException
	{

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", server.getPort());
		props.put("mail.smtp.host", server.getHost());
		props.put("mail.smtp.socketFactory.port", server.getPort());

		if (server.getTimeout() != null)
		{
			props.put("mail.smtp.timeout", server.getTimeout());
			props.put("mail.smtp.connectiontimeout", server.getTimeout());
		}

		if (server.getUseTLS())
			props.put("mail.smtp.starttls.enable", "true");

		if (server.getUseSSL())
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		jakarta.mail.Session session = jakarta.mail.Session.getInstance(props, new jakarta.mail.Authenticator()
		{
			@Override
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(server.getUsername(), server.getPassword());
			}
		});

		MimeMessage mimeMessage = new MimeMessage(session);
		mimeMessage.setFrom(sender);
		mimeMessage.setSubject(mail.getSubject());
		mimeMessage.setSentDate(new java.util.Date());

		if (mail.getPriority() == MimeMail.Priority.LOW)
			mimeMessage.setHeader("X-Priority", "5");
		else if (mail.getPriority() == MimeMail.Priority.HIGH)
			mimeMessage.setHeader("X-Priority", "1");

		mimeMessage.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(receiver));

		if (mail.getContent() instanceof MimeText mimeText)
		{
			mimeMessage.setText(mimeText.getText(), mimeText.getCharset(), mimeText.getSubType());
		} else if (mail.getContent() instanceof MimeDataFile mimeDataFile)
		{
			mimeMessage.setDisposition("Attachment");
			mimeMessage.setFileName(mimeDataFile.getName());
			mimeMessage.setContent(mimeDataFile.getData(), "application/octet-stream");
		} else if (mail.getContent() instanceof MimeList mimeList)
		{
			mimeMessage.setContent(getMultipart(mimeList));
		}

		mimeMessage.saveChanges();
		try (Transport transport = session.getTransport("smtp"))
		{
			if (!transport.isConnected())
				transport.connect();
			transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
		}

	}

	private MimeMultipart getMultipart(MimeList data) throws MessagingException
	{
		MimeMultipart mimeMultipart = new MimeMultipart();
		mimeMultipart.setSubType(data.getType());

		for (Mime mime : data)
		{
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			if (mime instanceof MimeText mimeText)
			{
				mimeBodyPart.setText(mimeText.getText(), mimeText.getCharset(), mimeText.getSubType());
			} else if (mime instanceof MimeDataFile mimeDataFile)
			{
				mimeBodyPart.setDisposition("Attachment");
				mimeBodyPart.setFileName(mimeDataFile.getName());
				mimeBodyPart.setContent(mimeDataFile.getData(), "application/octet-stream");
			} else if (mime instanceof MimeList mimeList)
			{
				mimeBodyPart.setContent(getMultipart(mimeList));
			}

			mimeMultipart.addBodyPart(mimeBodyPart);
		}

		return mimeMultipart;
	}
}