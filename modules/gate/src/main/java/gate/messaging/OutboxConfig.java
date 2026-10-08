package gate.messaging;

import io.smallrye.config.ConfigMapping;

import java.time.Duration;
import java.util.Optional;

/**
 * Configuration mapping for the unique outbound mail sender used by Gate.
 *
 * <p>This is an internal configuration contract used by the framework runtime and is not intended to be implemented
 * directly by applications.
 */
@ConfigMapping(prefix = "gate.messaging.outbox")
public interface OutboxConfig
{
	/**
	 * Gets the SMTP host used to send mail.
	 *
	 * @return the configured SMTP host
	 */
	Optional<String> host();

	/**
	 * Gets the SMTP port used to send mail.
	 *
	 * @return the configured SMTP port
	 */
	Optional<Integer> port();

	/**
	 * Gets the default sender username used by the outbox.
	 *
	 * @return the configured username
	 */
	Optional<String> username();

	/**
	 * Gets the SMTP authentication password.
	 *
	 * @return the configured password
	 */
	Optional<String> password();

	/**
	 * Gets the timeout used by SMTP operations.
	 *
	 * @return the configured timeout in the provider-supported format
	 */
	Optional<String> timeout();

	/**
	 * Indicates whether STARTTLS should be enabled for SMTP.
	 *
	 * @return an optional STARTTLS flag
	 */
	Optional<Boolean> tls();

	/**
	 * Indicates whether SSL should be enabled for SMTP.
	 *
	 * @return an optional SSL flag
	 */
	Optional<Boolean> ssl();

	/**
	 * Gets the maximum number of send attempts allowed for a queued mail.
	 *
	 * @return the optional maximum number of attempts
	 */
	Optional<Integer> maxAttempts();

	/**
	 * Gets the amount of time a queued mail may remain pending before expiring.
	 *
	 * <p>The value must be compatible with {@link Duration}, such as {@code PT24H} or {@code P1D}.
	 *
	 * @return the optional expiration duration
	 */
	Optional<Duration> expiration();

	/**
	 * Gets the base retry interval used for outbound backoff.
	 *
	 * <p>The next attempt is computed from the original queue time plus this interval multiplied by the number of
	 * attempts already performed.
	 *
	 * @return the optional retry interval
	 */
	Optional<Duration> retryInterval();
}
