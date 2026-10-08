package gate.language;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class English implements Language
{
	private static final Pattern SPACE_PATTERN = Pattern.compile(" +");
	private static final List<String> IGNORE = Arrays.asList("a", "an", "and", "as", "at", "by", "for", "in", "of", "on", "or", "the", "to");

	@Override
	public String capitalize(String string)
	{
		return string != null
				? Stream.of(SPACE_PATTERN.split(string))
				.collect(Collectors.collectingAndThen(Collectors.toList(), words ->
						words.isEmpty()
								? ""
								: Stream.concat(
								Stream.of(capitalizeToken(words.get(0), true)),
								words.stream().skip(1).map(word -> capitalizeToken(word, false)))
								.collect(Collectors.joining(" "))))
				: null;
	}

	private String capitalizeToken(String token, boolean force)
	{
		return Stream.of(token.split("-"))
				.collect(Collectors.collectingAndThen(Collectors.toList(), words ->
						words.isEmpty()
								? ""
								: Stream.concat(
								Stream.of(capitalizeWord(words.get(0), force)),
								words.stream().skip(1).map(word -> capitalizeWord(word, false)))
								.collect(Collectors.joining("-"))));
	}

	private String capitalizeWord(String string, boolean force)
	{
		string = string.toLowerCase();
		if (!force && IGNORE.contains(string))
			return string;
		StringBuilder builder = new StringBuilder(string);
		builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
		return builder.toString();
	}
}
