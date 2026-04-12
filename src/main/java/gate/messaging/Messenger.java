package gate.messaging;

import gate.annotation.Current;
import gate.catalog.MailCatalog;
import gate.entity.App;
import gate.entity.Mail;
import gate.error.AppException;
import gate.type.mime.MimeMail;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextListener;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Facade for Gate mail messaging.
 *
 * <p>This component exposes the public messaging API of the framework. It supports two delivery modes:
 * queued delivery via {@link #post(String, String, MimeMail)} and immediate delivery via
 * {@link #send(String, String, MimeMail)}. Incoming mailboxes configured in the application are also polled through
 * {@link #dispatch()} and converted into synchronous CDI {@link MailEvent} notifications.
 *
 * <p>The framework does not schedule mail processing automatically. Applications are expected to invoke
 * {@link #dispatch()} at the time and frequency that best fit their execution model.
 */
@ApplicationScoped
public class Messenger implements ServletContextListener
{
	private static final Duration DEFAULT_EXPIRATION = Duration.ofDays(1);
	
	@Inject
	@Current
	App app;

	@Inject
	Outbox outbox;

	@Inject
	Inboxes inboxes;

	/**
	 * Processes both outgoing and incoming mail flows.
	 *
	 * <p>For the outgoing flow, pending queued messages are sent using the configured outbox. For the incoming flow,
	 * every configured inbox is scanned and each received message is converted into a synchronous {@link MailEvent}.
	 */
	public void dispatch()
	{
		outbox.dispatch();
		inboxes.dispatch();
	}

	/**
	 * Queues a message for later delivery.
	 *
	 * @param sender   the sender address to persist with the queued message
	 * @param receiver the recipient address of the queued message
	 * @param message  the message to be queued
	 * @throws MessageException if the sender, receiver or message is missing, or if the queued message cannot be
	 *                          persisted
	 */
	public void post(String sender, String receiver, MimeMail<?> message) throws MessageException
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
			mail.setExpiration(mail.getDate().plus(outbox.getConfig().expiration().orElse(DEFAULT_EXPIRATION)));
			MailCatalog.insert(mail);
		} catch (AppException ex)
		{
			throw new MessageException(ex.getMessage());
		}
	}

	/**
	 * Queues a message for later delivery using the configured default sender.
	 *
	 * @param receiver the recipient address of the queued message
	 * @param mail     the message to be queued
	 * @throws MessageException if no default sender is configured or if the message cannot be queued
	 */
	public void post(String receiver, MimeMail<?> mail) throws MessageException
	{
		String sender = outbox.getConfig().username()
				.orElseThrow(() -> new MessageException("Tentativa de enviar mensagem sem remetente padrão configurado."));
		post(sender, receiver, mail);
	}

	/**
	 * Sends a message immediately.
	 *
	 * @param sender   the sender address to use when sending the message
	 * @param receiver the recipient address of the message
	 * @param mail     the message to be sent
	 * @throws MessageException if the message cannot be sent
	 */
	public void send(String sender, String receiver, MimeMail<?> mail) throws MessageException
	{
		outbox.send(sender, receiver, mail);
	}

	/**
	 * Sends a message immediately using the configured default sender.
	 *
	 * @param receiver the recipient address of the message
	 * @param mail     the message to be sent
	 * @throws MessageException if no default sender is configured or if the message cannot be sent
	 */
	public void send(String receiver, MimeMail<?> mail) throws MessageException
	{
		String sender = outbox.getConfig().username()
				.orElseThrow(() -> new MessageException("Tentativa de enviar mensagem sem remetente padrão configurado."));
		send(sender, receiver, mail);
	}
}