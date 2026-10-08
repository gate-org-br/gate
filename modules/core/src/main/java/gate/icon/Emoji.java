package gate.icon;

import gate.annotation.Icon;
import java.util.Objects;

/**
 *
 * @author davinunesdasilva
 */
public class Emoji implements Glyph
{

	private final String code;

	public Emoji(String code)
	{
		this.code = code;
	}

	@Override
	public String getCode()
	{
		return code;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Icon && Objects.equals(code, ((Emoji) obj).code);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(code);
	}

	@Override
	public String toString()
	{
		return "&#X" + getCode() + ";";
	}

}
