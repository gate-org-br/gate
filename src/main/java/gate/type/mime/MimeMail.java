package gate.type.mime;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Immutable mail message model used by Gate messaging.
 *
 * <p>A {@code MimeMail} contains a subject, a priority, a MIME content tree and optional mail headers. Header names
 * are normalized to lowercase and repeated headers accumulate values under the same key.
 *
 * @param <T> the root MIME type of the message content
 */
public final class MimeMail<T extends Mime> implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	private final T content;
	private final String subject;
	private final Priority priority;
	private final Map<String, List<String>> headers;

	private MimeMail(Priority priority, String subject, T content, Map<String, List<String>> headers)
	{
		this.subject = Objects.requireNonNull(subject, "Mime mail subject cannot be null");
		this.content = Objects.requireNonNull(content, "Mime mail content cannot be null");
		this.priority = Objects.requireNonNull(priority, "Mime mail priority cannot be null");
		this.headers = Collections.unmodifiableMap(Objects.requireNonNull(headers));
	}

	/**
	 * Creates a builder for a new {@code MimeMail}.
	 *
	 * @return a new mail builder
	 */
	public static Builder builder()
	{
		return new Builder();
	}

	/**
	 * Gets the mail subject.
	 *
	 * @return the subject
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * Gets the root MIME content of the mail.
	 *
	 * @return the root MIME content
	 */
	public T getContent()
	{
		return content;
	}

	/**
	 * Gets the mail priority.
	 *
	 * @return the configured priority
	 */
	public Priority getPriority()
	{
		return priority;
	}

	/**
	 * Gets the immutable map of mail headers.
	 *
	 * <p>Header names are normalized to lowercase.
	 *
	 * @return the mail headers
	 */
	public Map<String, List<String>> getHeaders()
	{
		return headers;
	}

	/**
	 * Gets all values associated with a header name.
	 *
	 * @param name the header name
	 *
	 * @return the values associated with the normalized header name
	 */
	public Optional<List<String>> getHeaderValues(String name) {return Optional.ofNullable(headers.get(normalizeHeaderName(name)));}

	/**
	 * Gets the first value associated with a header name.
	 *
	 * @param name the header name
	 *
	 * @return the first value associated with the normalized header name
	 */
	public Optional<String> getHeaderValue(String name)
	{
		return getHeaderValues(name).flatMap(values -> values.stream().findFirst());
	}

	@Override
	public String toString()
	{
		return subject;
	}

	private static String normalizeHeaderName(String name)
	{
		return Objects.requireNonNull(name, "Mime mail header name cannot be null")
				.toLowerCase(Locale.ROOT);
	}

	public enum Priority
	{
		LOW, NORMAL, HIGH
	}

	/**
	 * Builder for immutable {@link MimeMail} instances.
	 */
	public static final class Builder
	{
		private Mime content;
		private String subject;
		private Priority priority = Priority.NORMAL;
		private final Map<String, List<String>> headers = new LinkedHashMap<>();

		private Builder()
		{
		}

		/**
		 * Sets the mail subject.
		 *
		 * @param subject the mail subject
		 *
		 * @return this builder
		 */
		public Builder subject(String subject)
		{
			this.subject = subject;
			return this;
		}

		/**
		 * Sets the mail priority.
		 *
		 * @param priority the mail priority
		 *
		 * @return this builder
		 */
		public Builder priority(Priority priority)
		{
			this.priority = priority;
			return this;
		}

		/**
		 * Sets the root MIME content of the mail.
		 *
		 * @param content the root MIME content
		 *
		 * @return this builder
		 */
		public Builder content(Mime content)
		{
			this.content = content;
			return this;
		}

		/**
		 * Adds a header value to the mail.
		 *
		 * <p>If the same header is added multiple times, values are accumulated under the same normalized header name.
		 *
		 * @param name the header name
		 * @param value the header value
		 *
		 * @return this builder
		 */
		public Builder headers(String name, String value)
		{
			Objects.requireNonNull(value, "Mime mail header value cannot be null");
			headers.computeIfAbsent(normalizeHeaderName(name), key -> new ArrayList<>())
					.add(value);
			return this;
		}

		/**
		 * Adds all values from the given header map to the mail.
		 *
		 * @param headers the headers to add
		 *
		 * @return this builder
		 */
		public Builder headers(Map<String, List<String>> headers)
		{
			Objects.requireNonNull(headers, "Mime mail headers cannot be null");
			headers.forEach((name, values) ->
			{
				Objects.requireNonNull(values, "Mime mail header values cannot be null");
				values.forEach(value -> headers(name, value));
			});
			return this;
		}

		/**
		 * Builds an immutable {@link MimeMail} instance.
		 *
		 * @param <T> the root MIME type of the resulting mail
		 *
		 * @return the built mail
		 */
		@SuppressWarnings("unchecked")
		public <T extends Mime> MimeMail<T> build()
		{
			Map<String, List<String>> immutableHeaders = new LinkedHashMap<>();
			headers.forEach((name, values) -> immutableHeaders.put(name, List.copyOf(values)));
			return new MimeMail<>(priority, subject, (T) content, immutableHeaders);
		}
	}
}
