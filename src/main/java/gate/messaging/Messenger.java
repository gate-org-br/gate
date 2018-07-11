package gate.messaging;

import gate.type.mime.Mime;
import gate.type.DataFile;
import gate.type.mime.MimeList;
import gate.type.mime.MimeMail;
import gate.type.mime.MimeText;
import gate.type.DateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Singleton
public class Messenger
{

	private volatile ScheduledExecutorService service;

	public void startup()
	{
		service = Executors.newSingleThreadScheduledExecutor();

		service.scheduleWithFixedDelay(() ->
		{
			try
			{
				Context context = new InitialContext();
				Queue queue = (Queue) context.lookup("java:/jms/queue/MailBox");
				ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("/ConnectionFactory");

				try (Connection connection = connectionFactory.createConnection();
						Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
						MessageConsumer consumer = session.createConsumer(queue))

				{
					connection.start();
					ObjectMessage objectMessage
							= (ObjectMessage) consumer.receiveNoWait();
					if (objectMessage != null)
						send(objectMessage.getStringProperty("sender"),
								objectMessage.getStringProperty("receiver"),
								objectMessage.getBody(MimeMail.class));
				}
			} catch (NamingException | JMSException | RuntimeException | MessagingException ex)
			{
				Logger.getLogger(Messenger.class.getName())
						.log(Level.SEVERE, null, ex);
			}
		}, 0, 60, TimeUnit.SECONDS);
	}

	@PreDestroy
	public void teardown()
	{
		if (service != null)
		{
			service.shutdownNow();
			service = null;
		}
	}

	public void post(String sender,
			String receiver,
			MimeMail<?> mail)
			throws MessageException
	{
		if (receiver == null)
			throw new MessageException("Esquecifique o destinatário da mensagem.");
		if (mail == null)
			throw new MessageException("Esquecifique o conteudo da mensagem a ser enviada.");
		try
		{
			Context context = new InitialContext();
			try
			{
				ConnectionFactory connectionFactory
						= (ConnectionFactory) context.lookup("/ConnectionFactory");
				try
				{
					Queue queue = (Queue) new InitialContext().lookup("java:/jms/queue/MailBox");

					try
					{
						javax.mail.Session mailSession = (javax.mail.Session) context.lookup("java:/comp/env/MailSession");
						try
						{

							if (sender == null
									&& mailSession.getProperties().get("mail.smtp.user") != null)
								sender = (String) mailSession.getProperties().get("mail.smtp.user");
							if (sender == null)
								throw new MessageException("Esquecifique o remetente da mensagem.");

							try (Connection connection = connectionFactory.createConnection();
									Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
									MessageProducer producer = session.createProducer(queue))
							{

								ObjectMessage message = session.createObjectMessage();
								message.setObject(mail);
								message.setStringProperty("date", LocalDateTime.now()
										.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
								message.setStringProperty("sender", sender);
								message.setStringProperty("receiver", receiver);

								connection.start();
								producer.send(message);
								if (service == null)
									startup();
							}
						} catch (JMSException ex)
						{
							throw new MessageException(ex, "Erro ao enviar messagem de %s para %s: "
									+ ex.getMessage(), sender, receiver);
						}
					} catch (NamingException ex)
					{
						throw new MessageException(ex, "O servidor de EMails java:/comp/env/MailSession não foi devidamente configurado neste servidor");
					}
				} catch (NamingException ex)
				{
					throw new MessageException(ex, "A fila java:/jms/queue/MailBox não foi devidamente configurada neste servidor");
				}
			} catch (NamingException ex)
			{
				throw new MessageException(ex, "O serviço JMS não foi devidamente configurado neste servidor");
			}
		} catch (NamingException ex)
		{
			throw new MessageException(ex, "O serviço de nomes não foi devidamente configurado neste servidor");
		}
	}

	public void post(String receiver,
			MimeMail<?> mail)
			throws MessageException
	{
		post(null, receiver, mail);
	}

	public List<Message> search() throws MessageException
	{
		try
		{
			Context context = new InitialContext();
			try
			{
				ConnectionFactory connectionFactory
						= (ConnectionFactory) context.lookup("/ConnectionFactory");
				try
				{
					Queue queue = (Queue) new InitialContext().lookup("java:/jms/queue/MailBox");
					try (Connection connection = connectionFactory.createConnection();
							javax.jms.Session session = connection.createSession(true,
									javax.jms.Session.AUTO_ACKNOWLEDGE);
							QueueBrowser browser = session.createBrowser(queue))
					{

						List<Message> messages = new ArrayList<>();
						for (Object object : Collections.list(browser.getEnumeration()))
						{
							ObjectMessage objectMessage = (ObjectMessage) object;
							LocalDateTime date = LocalDateTime
									.parse(objectMessage.getStringProperty("date"),
											DateTimeFormatter.ISO_LOCAL_DATE_TIME);
							String sender = objectMessage.getStringProperty("sender");
							String receiver = objectMessage.getStringProperty("receiver");
							MimeMail data = objectMessage.getBody(MimeMail.class);
							messages.add(new Message(date, sender, receiver, data));
						}
						return messages;
					} catch (JMSException ex)
					{
						throw new MessageException(ex, "Erro ao obter mensgens: " + ex.getMessage());
					}
				} catch (NamingException ex)
				{
					throw new MessageException(ex, "A fila java:/jms/queue/MailBox não foi devidamente configurada neste servidor");
				}

			} catch (NamingException ex)
			{
				throw new MessageException(ex, "O serviço JMS não foi devidamente configurado neste servidor");
			}
		} catch (NamingException ex)
		{
			throw new MessageException(ex, "O serviço de nomes não foi devidamente configurado neste servidor");
		}

	}

	private void send(String sender, String receiver, MimeMail mail)
			throws NamingException, MessagingException
	{
		javax.mail.Session mailSession = (javax.mail.Session) new InitialContext().lookup("java:/comp/env/MailSession");
		MimeMessage mimeMessage = new MimeMessage(mailSession);
		mimeMessage.setFrom(sender);
		mimeMessage.setSubject(mail.getSubject());
		mimeMessage.setSentDate(new DateTime().toDate());
		mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(receiver));

		if (mail.getContent() instanceof MimeText)
		{
			MimeText mimeText = (MimeText) mail.getContent();
			mimeMessage.setText(mimeText.getText(), mimeText.getCharset(), mimeText.getSubType());
		} else if (mail.getContent() instanceof DataFile)
		{
			DataFile mimeDataFile = (DataFile) mail.getContent();
			mimeMessage.setDisposition("Attachment");
			mimeMessage.setFileName(mimeDataFile.getName());
			mimeMessage.setContent(mimeDataFile.getData(), "application/octet-stream");
		} else if (mail.getContent() instanceof MimeList)
		{
			MimeList mimeList = (MimeList) mail.getContent();
			mimeMessage.setContent(getMultipart(mimeList));
		}

		Transport.send(mimeMessage);

	}

	private MimeMultipart getMultipart(MimeList data) throws MessagingException
	{
		MimeMultipart mimeMultipart = new MimeMultipart();
		mimeMultipart.setSubType(data.getType());

		for (Mime mime : data)
		{
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			if (mime instanceof MimeText)
			{
				MimeText mimeText = (MimeText) mime;
				mimeBodyPart.setText(mimeText.getText(), mimeText.getCharset(), mimeText.getSubType());
			} else if (mime instanceof DataFile)
			{
				DataFile mimeDataFile = (DataFile) mime;
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
