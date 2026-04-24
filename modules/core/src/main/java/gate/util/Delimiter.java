package gate.util;

/**
 * Adds and removes fixed delimiters around string values.
 * <p>
 * The predefined constants cover common symmetric and asymmetric delimiters and
 * {@link #NONE} behaves as a no-op.
 *
 * @param start token used at the start of a value
 * @param end   token used at the end of a value
 */
public record Delimiter(String start, String end)
{
	public static final Delimiter NONE = new Delimiter("", "");
	public static final Delimiter DOUBLE = new Delimiter("\"");
	public static final Delimiter SINGLE = new Delimiter("'");
	public static final Delimiter BACKTICK = new Delimiter("`");
	public static final Delimiter BRACKET = new Delimiter("[", "]");
	public static final Delimiter PARENTHESIS = new Delimiter("(", ")");
	public static final Delimiter BRACES = new Delimiter("{", "}");

	public Delimiter(String delimiter)
	{
		this(delimiter, delimiter);
	}

	/**
	 * Creates a delimitter for the informed tokens.
	 *
	 * @throws NullPointerException when {@code start} or {@code end} is {@code null}
	 */
	public Delimiter
	{
		if (start == null)
			throw new NullPointerException("start can't be null");

		if (end == null)
			throw new NullPointerException("end can't be null");
	}

	/**
	 * Wraps the informed value with the configured delimiters.
	 *
	 * @param value value to delimit
	 * @return delimited value or {@code null} when the input is {@code null}
	 */
	public String insert(String value)
	{
		return value != null ? start + value + end : null;
	}

	/**
	 * Removes the configured delimiters from both sides of the informed value
	 * when it is fully delimited.
	 *
	 * @param value value to remove delimiters from
	 * @return undelimited value, the original value when it is not fully
	 * delimited, or {@code null} when the input is {@code null}
	 */
	public String remove(String value)
	{
		return value != null
		       && value.length() >= start.length() + end.length()
		       && value.startsWith(start)
		       && value.endsWith(end)
				? value.substring(start.length(), value.length() - end.length())
				: value;
	}

	/**
	 * Removes any supported quote delimiter pair from both sides of the
	 * informed value when it is fully quoted.
	 *
	 * @param value value to remove quotes from
	 * @return unquoted value, the original value when it is not fully quoted,
	 * or {@code null} when the input is {@code null}
	 */
	public static String unquote(String value)
	{
		if (value == null || value.length() < 2)
			return value;

		char first = value.charAt(0);
		char last = value.charAt(value.length() - 1);

		return first == last && (first == '"' || first == '\'' || first == '`')
				? value.substring(1, value.length() - 1)
				: value;
	}
}