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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContextListener;
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
						send(mail.getSender(),
								mail.getReceiver(), mail.getMessage());
						control.delete(mail);
					} catch (MessagingException | RuntimeException ex)
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
			if (receiver == null)
				throw new MessageException("Esquecifique o destinatário da mensagem.");
			if (message == null)
				throw new MessageException("Esquecifique o conteudo da mensagem a ser enviada.");

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

	public void post(String receiver,
			MimeMail<?> mail)
			throws MessageException
	{
		try
		{
			Server server = control.server();
			post(server.getUsername(), receiver, mail);
		} catch (AppException ex)
		{
			throw new MessageException(ex.getMessage());
		}
	}

	public List<Mail> search() throws MessageException
	{
		return control.search();
	}

	private void send(String sender, String receiver, MimeMail<?> mail) throws MessagingException
	{
		try
		{
			send(control.server(), sender, receiver, mail);
		} catch (AppException ex)
		{
			throw new MessagingException(ex.getMessage());
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

		javax.mail.Session session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator()
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

		mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(receiver));

		if (mail.getContent() instanceof MimeText mimeText)
		{
			mimeMessage.setText(mimeText.getText(), mimeText.getCharset(), mimeText.getContentType().getSubtype());
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
		mimeMultipart.setSubType(data.getContentType().getSubtype());

		for (Mime mime : data)
		{
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			if (mime instanceof MimeText)
			{
				MimeText mimeText = (MimeText) mime;
				mimeBodyPart.setText(mimeText.getText(), mimeText.getCharset(), mimeText.getContentType().getSubtype());
			} else if (mime instanceof MimeDataFile)
			{
				MimeDataFile mimeDataFile = (MimeDataFile) mime;
				mimeBodyPart.setDisposition("Attachment");
				mimeBodyPart.setFileName(mimeDataFile.getName());
				mimeBodyPart.setContent(mimeDataFile.getData(), "application/octet-stream");
			} else if (mime instanceof MimeList)
			{
				MimeList mimeList = (MimeList) mime;
				mimeBodyPart.setContent(getMultipart(mimeList));
			}

			mimeMultipart.addBodyPart(mimeBodyPart);
		}

		return mimeMultipart;
	}
}
