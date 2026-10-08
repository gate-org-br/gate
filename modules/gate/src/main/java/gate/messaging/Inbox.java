package gate.messaging;

import gate.lang.contentType.ContentType;
import gate.type.mime.*;
import jakarta.enterprise.event.Event;
import jakarta.mail.*;
import jakarta.mail.Flags.Flag;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

record Inbox(String receiver,
             String folder,
             Properties properties,
             Event<MailEvent> event)
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Inbox.class);

	public static Inbox of(InboxConfig.Account inbound,
	                       Event<MailEvent> event)
	{
		Properties properties = new Properties();
		properties.put("mail.store.protocol", inbound.protocol());
		properties.put("mail." + inbound.protocol() + ".host", inbound.host());
		properties.put("mail." + inbound.protocol() + ".port", Integer.toString(inbound.port()));
		properties.put("mail." + inbound.protocol() + ".user", inbound.username());
		properties.put("mail." + inbound.protocol() + ".password", inbound.password());
		if (inbound.tls().orElse(false))
			properties.put("mail." + inbound.protocol() + ".starttls.enable", "true");
		if (inbound.ssl().orElse(false))
			properties.put("mail." + inbound.protocol() + ".ssl.enable", "true");

		return new Inbox(
				inbound.receiver(),
				inbound.folder().orElse("INBOX"),
				properties,
				event);
	}

	public void dispatch()
	{
		try
		{
			Session session = Session.getInstance(properties);

			try (Store store = session.getStore((String) properties.get("mail.store.protocol")))
			{
				store.connect();

				try (Folder mailFolder = store.getFolder(folder))
				{
					mailFolder.open(Folder.READ_WRITE);
					for (Message message : mailFolder.getMessages())
						process(message);
				}
			}
		} catch (RuntimeException | MessagingException | IOException ex)
		{
			LOGGER.error(ex.getMessage(), ex);
		}
	}

	private void process(Message message)
			throws MessagingException, IOException
	{
		Address[] from = message.getFrom();
		if (from == null || from.length == 0)
			throw new MessagingException("Received mail message without sender");
		var sender = from[0] instanceof InternetAddress address ? address.getAddress() : from[0].toString();

		event.fire(new MailEvent(sender, receiver, toMimeMail(message)));
		message.setFlag(Flag.DELETED, true);
	}

	private MimeMail<?> toMimeMail(Message message) throws MessagingException, IOException
	{
		return MimeMail.builder()
				.subject(Objects.requireNonNullElse(message.getSubject(), ""))
				.priority(getPriority(message))
				.content(getMime(message))
				.headers(Collections.list(message.getAllHeaders())
						.stream()
						.collect(Collectors.groupingBy(Header::getName,
								Collectors.mapping(Header::getValue, Collectors.toList()))))
				.build();
	}

	private Mime getMime(Part part) throws MessagingException, IOException
	{
		Object content = part.getContent();
		ContentType contentType = getContentType(part);

		if (content instanceof Multipart multipart)
		{
			MimeList mimeList = MimeList.of(contentType);
			for (int i = 0; i < multipart.getCount(); i++)
				mimeList.add(getMime(multipart.getBodyPart(i)));
			return mimeList;
		}

		if (content instanceof String text)
		{
			String charset = contentType.getParameters().getOrDefault("charset", "UTF-8");
			String fileName = getFileName(part);
			if (fileName != null)
				return MimeTextFile.of(contentType, charset, text, fileName);
			return MimeText.of(contentType, charset, text);
		}

		if (content instanceof InputStream stream)
		{
			try (InputStream input = stream)
			{
				return getBinaryMime(part, contentType, input.readAllBytes());
			}
		}

		if (content instanceof byte[] bytes)
			return getBinaryMime(part, contentType, bytes);

		return MimeText.of(contentType, "UTF-8", Objects.toString(content, ""));
	}

	private Mime getBinaryMime(Part part, ContentType contentType, byte[] bytes) throws MessagingException
	{
		String fileName = getFileName(part);
		if (fileName != null)
			return MimeDataFile.of(contentType, bytes, fileName);
		return MimeData.of(contentType, bytes);
	}


	private String getFileName(Part part) throws MessagingException
	{
		String fileName = part.getFileName();
		if (fileName == null)
			return null;
		try
		{
			return MimeUtility.decodeText(fileName);
		} catch (Exception ex)
		{
			throw new MessagingException(ex.getMessage(), ex);
		}
	}

	private MimeMail.Priority getPriority(Message message) throws MessagingException
	{
		String[] priorities = message.getHeader("X-Priority");
		if (priorities == null || priorities.length == 0)
			return MimeMail.Priority.NORMAL;
		if (priorities[0].startsWith("1") || priorities[0].startsWith("2"))
			return MimeMail.Priority.HIGH;
		if (priorities[0].startsWith("4") || priorities[0].startsWith("5"))
			return MimeMail.Priority.LOW;
		return MimeMail.Priority.NORMAL;
	}

	private ContentType getContentType(Part part) throws MessagingException
	{
		ContentType fallback = part.isMimeType("multipart/*")
				? ContentType.of("multipart", "mixed")
				: part.isMimeType("text/*")
				  ? ContentType.of("text", "plain")
				  : ContentType.of("application", "octet-stream");
		String value = part.getContentType();
		if (value == null)
			return fallback;
		try
		{
			return ContentType.valueOf(value);
		} catch (RuntimeException ex)
		{
			return fallback;
		}
	}
}
