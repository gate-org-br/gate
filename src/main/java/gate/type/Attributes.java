package gate.type;

import gate.converter.Converter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@SuppressWarnings("serial")
public class Attributes extends HashMap<String, Object>
{

	public Attributes()
	{

	}

	public Attributes(Map<String, Object> map)
	{
		super(map);
	}

	@Override
	public String toString()
	{
		StringJoiner str = new StringJoiner(" ");
		for (Map.Entry<String, Object> entry : entrySet())
			if (entry.getValue() != null && !"".equals(entry.getValue()))
				str.add(entry.getKey() + "='" + Converter.toString(entry.getValue()).replaceAll("'", "\"") + "'");
		return str.toString();
	}
}
