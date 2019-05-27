package gate.util;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class DurationFormatter
{

	protected final static long S = 1;
	protected final static long M = 60 * S;
	protected final static long H = 60 * M;

	public static final Pattern PATTERN = Pattern.compile("^([0-9]+):([0-9]+):([0-9]+)$");

	public static String format(Duration duration)
	{
		long value = duration.getSeconds();
		long h = value / H;
		long m = (value % H) / M;
		long s = ((value % H) % M) / S;
		return String.format("%02d:%02d:%02d", h, m, s);
	}

	public static Duration parse(String string)
	{
		int i = 0;
		long value = 0;
		long field = -1;

		while (i < string.length())
			switch (string.charAt(i++))
			{
				case '0':
					field = field == -1 ? 0 : (field * 10) + 0;
					break;
				case '1':
					field = field == -1 ? 1 : (field * 10) + 1;
					break;
				case '2':
					field = field == -1 ? 2 : (field * 10) + 2;
					break;
				case '3':
					field = field == -1 ? 3 : (field * 10) + 3;
					break;
				case '4':
					field = field == -1 ? 4 : (field * 10) + 4;
					break;
				case '5':
					field = field == -1 ? 5 : (field * 10) + 5;
					break;
				case '6':
					field = field == -1 ? 6 : (field * 10) + 6;
					break;
				case '7':
					field = field == -1 ? 7 : (field * 10) + 7;
					break;
				case '8':
					field = field == -1 ? 8 : (field * 10) + 8;
					break;
				case '9':
					field = field == -1 ? 9 : (field * 10) + 9;
					break;
				case ':':
					if (field == -1)
						throw new DateTimeParseException(string + " não é uma duração válida", string, 0);
					value = value * 60 + field * 60;
					field = -1;
					break;
				default:
					throw new DateTimeParseException(string + " não é uma duração válida", string, 0);
			}

		if (field == -1)
			throw new DateTimeParseException(string + " não é uma duração válida", string, 0);
		return Duration.ofSeconds(value + field);
	}
}
