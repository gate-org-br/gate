package gate.type;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class SafeStyle implements Serializable
{
	@Serial
	private static final long serialVersionUID = 1L;

	private static final Pattern COLOR = Pattern.compile("(?i)^(#[0-9a-f]{3}|#[0-9a-f]{6}|rgb\\(\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*\\)|rgba\\(\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*,\\s*(0|1|0?\\.\\d+)\\s*\\)|[a-z]+)$");
	private static final Pattern LENGTH = Pattern.compile("(?i)^-?(\\d+(?:\\.\\d+)?)(px|em|rem|%|pt)?$");
	private static final Pattern LENGTH_LIST = Pattern.compile("(?i)^-?(\\d+(?:\\.\\d+)?)(px|em|rem|%|pt)?(?:\\s+-?(\\d+(?:\\.\\d+)?)(px|em|rem|%|pt)?){0,3}$");
	private static final Pattern FONT_WEIGHT = Pattern.compile("(?i)^(normal|bold|[1-9]00)$");
	private static final Pattern FONT_STYLE = Pattern.compile("(?i)^(normal|italic|oblique)$");
	private static final Pattern TEXT_DECORATION = Pattern.compile("(?i)^(none|underline|line-through|overline)$");
	private static final Pattern TEXT_ALIGN = Pattern.compile("(?i)^(left|right|center|justify)$");
	private static final Pattern BORDER_STYLE = Pattern.compile("(?i)^(none|solid|dashed|dotted|double)$");
	private static final Pattern BORDER = Pattern.compile("(?i)^((\\d+(?:\\.\\d+)?)(px|pt)\\s+)?(none|solid|dashed|dotted|double)(\\s+(#[0-9a-f]{3}|#[0-9a-f]{6}|[a-z]+))?$");

	private static final Map<String, Predicate<String>> RULES = new LinkedHashMap<>();

	static
	{
		RULES.put("color", SafeStyle::isColor);
		RULES.put("background-color", SafeStyle::isColor);
		RULES.put("font-weight", value -> FONT_WEIGHT.matcher(value).matches());
		RULES.put("font-style", value -> FONT_STYLE.matcher(value).matches());
		RULES.put("text-decoration", value -> TEXT_DECORATION.matcher(value).matches());
		RULES.put("text-align", value -> TEXT_ALIGN.matcher(value).matches());
		RULES.put("margin", SafeStyle::isLengthList);
		RULES.put("margin-top", SafeStyle::isLength);
		RULES.put("margin-right", SafeStyle::isLength);
		RULES.put("margin-bottom", SafeStyle::isLength);
		RULES.put("margin-left", SafeStyle::isLength);
		RULES.put("padding", SafeStyle::isLengthList);
		RULES.put("padding-top", SafeStyle::isLength);
		RULES.put("padding-right", SafeStyle::isLength);
		RULES.put("padding-bottom", SafeStyle::isLength);
		RULES.put("padding-left", SafeStyle::isLength);
		RULES.put("border", value -> BORDER.matcher(value).matches());
		RULES.put("border-top", value -> BORDER.matcher(value).matches());
		RULES.put("border-right", value -> BORDER.matcher(value).matches());
		RULES.put("border-bottom", value -> BORDER.matcher(value).matches());
		RULES.put("border-left", value -> BORDER.matcher(value).matches());
		RULES.put("border-width", SafeStyle::isLength);
		RULES.put("border-style", value -> BORDER_STYLE.matcher(value).matches());
		RULES.put("border-color", SafeStyle::isColor);
		RULES.put("width", SafeStyle::isLength);
		RULES.put("height", SafeStyle::isLength);
		RULES.put("min-width", SafeStyle::isLength);
		RULES.put("max-width", SafeStyle::isLength);
		RULES.put("min-height", SafeStyle::isLength);
		RULES.put("max-height", SafeStyle::isLength);
	}

	private final String value;

	private SafeStyle(String value) {this.value = value;}

	public static SafeStyle of(String value)
	{
		return valueOf(value);
	}

	public static SafeStyle valueOf(String value)
	{
		Objects.requireNonNull(value, "value");
		value = value.trim()
				.replaceAll("\\s*;\\s*", ";")
				.replaceAll("\\s*:\\s*", ":")
				.replaceAll("\\s+", " ");

		var style = new StringJoiner("; ");
		for (String declaration : value.split(";"))
		{
			if (declaration.isEmpty())
				continue;

			int separator = declaration.indexOf(':');
			if (separator <= 0)
				continue;

			String property = declaration.substring(0, separator).toLowerCase();
			String cssValue = declaration.substring(separator + 1);

			Predicate<String> validator = RULES.get(property);
			if (validator == null || !validator.test(cssValue))
				continue;

			style.add(property + ": " + cssValue);
		}

		return new SafeStyle(style.toString());
	}

	public boolean isBlank() {return value.isBlank();}

	@Override
	public String toString() {return value;}

	@Override
	public boolean equals(Object obj) {return obj instanceof SafeStyle other && value.equals(other.value);}

	@Override
	public int hashCode() {return value.hashCode();}


	private static boolean isColor(String value) {return isGloballySafe(value) && COLOR.matcher(value).matches();}

	private static boolean isLength(String value) {return isGloballySafe(value) && LENGTH.matcher(value).matches();}

	private static boolean isLengthList(String value) {return isGloballySafe(value) && LENGTH_LIST.matcher(value).matches();}

	private static boolean isGloballySafe(String value)
	{
		String normalized = value.toLowerCase();
		return !normalized.contains("url(")
		       && !normalized.contains("expression(")
		       && !normalized.contains("javascript:")
		       && !normalized.contains("@import")
		       && !normalized.contains("behavior")
		       && !normalized.contains("-moz-binding")
		       && normalized.indexOf('<') < 0
		       && normalized.indexOf('>') < 0
		       && normalized.indexOf('"') < 0
		       && normalized.indexOf('\'') < 0
		       && normalized.indexOf('\\') < 0;
	}
}
