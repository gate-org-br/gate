package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.PortugueseTextConverter;
import java.util.regex.Pattern;

@Converter(PortugueseTextConverter.class)
public class PortugueseText
{

	private final String value;
	public static final Pattern PATTERN
		= Pattern.compile("^[0123456789ºªa-zA-ZàÀáÁâÂãÃéÉêÊíÍóÓôÔõÕúÚçÇ&., \n\r\t]*$");

	private PortugueseText(String value)
	{
		this.value = value;
	}

	public static PortugueseText valueOf(String value)
	{
		if (value == null)
			throw new IllegalArgumentException(value + " is not a valid portuguese  text");
		value = value.chars()
			.filter(e -> PortugueseText.validate(e))
			.collect(StringBuilder::new, StringBuilder::appendCodePoint,
				StringBuilder::append).toString();
		return new PortugueseText(value);
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof PortugueseText
			&& ((PortugueseText) obj).value.equals(value);
	}

	@Override
	public String toString()
	{
		return value;
	}

	private static boolean validate(int c)
	{
		return c == 'a'
			|| c == 'b'
			|| c == 'c'
			|| c == 'd'
			|| c == 'e'
			|| c == 'f'
			|| c == 'g'
			|| c == 'h'
			|| c == 'i'
			|| c == 'j'
			|| c == 'k'
			|| c == 'l'
			|| c == 'm'
			|| c == 'n'
			|| c == 'o'
			|| c == 'p'
			|| c == 'q'
			|| c == 'r'
			|| c == 's'
			|| c == 't'
			|| c == 'u'
			|| c == 'v'
			|| c == 'w'
			|| c == 'x'
			|| c == 'y'
			|| c == 'z'
			|| c == 'A'
			|| c == 'B'
			|| c == 'C'
			|| c == 'D'
			|| c == 'E'
			|| c == 'F'
			|| c == 'G'
			|| c == 'H'
			|| c == 'I'
			|| c == 'J'
			|| c == 'K'
			|| c == 'L'
			|| c == 'M'
			|| c == 'N'
			|| c == 'O'
			|| c == 'P'
			|| c == 'Q'
			|| c == 'R'
			|| c == 'S'
			|| c == 'T'
			|| c == 'U'
			|| c == 'V'
			|| c == 'W'
			|| c == 'X'
			|| c == 'Y'
			|| c == 'Z'
			|| c == '0'
			|| c == '1'
			|| c == '2'
			|| c == '3'
			|| c == '4'
			|| c == '5'
			|| c == '6'
			|| c == '7'
			|| c == '8'
			|| c == '9'
			|| c == 'á'
			|| c == 'é'
			|| c == 'í'
			|| c == 'ó'
			|| c == 'ú'
			|| c == 'Á'
			|| c == 'È'
			|| c == 'Í'
			|| c == 'Ó'
			|| c == 'Ú'
			|| c == 'â'
			|| c == 'ê'
			|| c == 'ô'
			|| c == 'Â'
			|| c == 'Ê'
			|| c == 'Ô'
			|| c == 'ã'
			|| c == 'õ'
			|| c == 'Ã'
			|| c == 'Õ'
			|| c == 'à'
			|| c == 'À'
			|| c == 'ç'
			|| c == 'Ç'
			|| c == '&'
			|| c == ' '
			|| c == 'º'
			|| c == 'ª'
			|| c == '.'
			|| c == ','
			|| c == '\n'
			|| c == '\r'
			|| c == '\t';
	}
}
