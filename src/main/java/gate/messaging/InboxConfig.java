package gate.messaging;

import io.smallrye.config.ConfigMapping;

import java.util.List;
import java.util.Optional;

/**
 * Configuration mapping for all inbound mailboxes monitored by Gate.
 *
 * <p>Each configured inbox represents one logical receiver. The receiver is used by the framework as the routing key
 * associated with messages read from that mailbox.
 *
 * <p>This is an internal configuration contract used by the framework runtime and is not intended to be implemented
 * directly by applications.
 */
@ConfigMapping(prefix = "gate.messaging")
public interface InboxConfig
{
	/**
	 * Gets the list of configured inbound mailboxes.
	 *
	 * @return the configured inbound mailboxes
	 */
	List<Account> inbox();

	/**
	 * Configuration of a single inbound mailbox.
	 */
	interface Account
	{
		/**
		 * Gets the logical receiver associated with this mailbox.
		 *
		 * @return the receiver used when firing {@link MailEvent}
		 */
		String receiver();

		/**
		 * Gets the mail store protocol used to access this mailbox.
		 *
		 * @return the configured protocol, such as {@code imap}, {@code imaps}, {@code pop3} or {@code pop3s}
		 */
		String protocol();

		/**
		 * Gets the mail server host.
		 *
		 * @return the host used to connect to the mailbox
		 */
		String host();

		/**
		 * Gets the mail server port.
		 *
		 * @return the configured connection port
		 */
		int port();

		/**
		 * Gets the username used to authenticate in the mailbox.
		 *
		 * @return the configured username
		 */
		String username();

		/**
		 * Gets the password used to authenticate in the mailbox.
		 *
		 * @return the configured password
		 */
		String password();

		/**
		 * Indicates whether STARTTLS should be enabled for this mailbox.
		 *
		 * @return an optional flag for STARTTLS
		 */
		Optional<Boolean> tls();

		/**
		 * Indicates whether SSL should be enabled for this mailbox.
		 *
		 * @return an optional flag for SSL
		 */
		Optional<Boolean> ssl();

		/**
		 * Gets the folder to be monitored.
		 *
		 * <p>This option is primarily useful for IMAP-compatible protocols. When omitted, the framework uses the
		 * default {@code INBOX} folder.
		 *
		 * @return the configured folder name
		 */
		Optional<String> folder();
	}
}
