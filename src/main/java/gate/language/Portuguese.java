package gate.language;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides Portuguese language utility methods.
 */
class Portuguese implements Language
{

	private static final Pattern PATTERN = Pattern.compile(" +");
	private static final List<String> IGNORE = Arrays.asList("e", "de", "do", "da", "dos", "das");

	@Override
	public String capitalizeWord(String string)
	{
		string = string.toLowerCase();
		if (IGNORE.contains(string))
			return string;
		StringBuilder builder = new StringBuilder(string);
		builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
		return builder.toString();
	}

	@Override
	public String capitalizeName(String string)
	{
		return Stream.of(PATTERN.split(string))
				.map(this::capitalizeWord)
				.collect(Collectors.joining(" "));
	}
}
