package gate.messaging;

import gate.type.mime.MimeMail;

import java.util.Objects;

/**
 * Synchronous CDI event fired for each received email message.
 *
 * @param sender the sender address extracted from the received email
 * @param receiver the configured inbox receiver associated with the mailbox that received the email
 * @param mail the received mail converted to the Gate {@link MimeMail} model
 */
public record MailEvent(String sender, String receiver, MimeMail<?> mail)
{

	public MailEvent
	{
		Objects.requireNonNull(sender, "Mail event sender cannot be null");
		Objects.requireNonNull(receiver, "Mail event receiver cannot be null");
		Objects.requireNonNull(mail, "Mail event mail cannot be null");
	}
}
