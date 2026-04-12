package gate.sql;

import gate.util.Delimiter;
import gate.util.SystemProperty;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class Formatter
{
	private static final Delimiter IDENTIFIER_DELIMITER = SystemProperty.get("gate.sql.identifier-delimiter")
			.map(string -> switch (string.isBlank() ? "NONE" : string.toUpperCase(Locale.ROOT))
			{
				case "NONE" -> Delimiter.NONE;
				case "DOUBLE" -> Delimiter.DOUBLE;
				case "BACKTICK" -> Delimiter.BACKTICK;
				case "BRACKET" -> Delimiter.BRACKET;
				default -> throw new IllegalArgumentException("Unsupported SQL delimiter: " + string);
			})
			.orElse(Delimiter.NONE);

	public static String sql(String sql, Object... args)
	{
		int index = 0;
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < sql.length(); i++)
			if (sql.charAt(i) == '@')
				result.append(args[index++].toString());
			else
				result.append(sql.charAt(i));
		return result.toString();
	}

	public static String identifier(String identifier)
	{
		Objects.requireNonNull(identifier);
		return Arrays.stream(identifier.split("\\.", -1))
				.map(e -> e.equals("*") || e.isEmpty() ? e : IDENTIFIER_DELIMITER.insert(e))
				.collect(Collectors.joining("."));
	}

}