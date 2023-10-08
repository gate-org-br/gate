package gate.icon;

import gate.lang.json.JsonObject;
import java.util.Objects;

public class Icon implements Glyph
{

	private final String code;
	private final String name;

	public Icon(String code, String name)
	{
		this.code = code;
		this.name = name;
	}

	@Override
	public String getCode()
	{
		return code;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Icon && Objects.equals(code, ((Icon) obj).code) && Objects.equals(name, ((Icon) obj).name);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(code) + Objects.hashCode(name);
	}

	@Override
	public String toString()
	{
		return "&#X" + getCode() + ";";
	}

	public JsonObject toJson()
	{
		return new JsonObject()
			.setString("code", code)
			.setString("name", name);
	}
}
