package gate.tags;

import gate.util.Parameters;

public class ParameterTag extends AttributeTag
{

	private final Parameters parameters = new Parameters();

	@Override
	public void setDynamicAttribute(String uri, String name, Object value)
	{
		if (name.startsWith("_") && name.length() > 1)
			getParameters().put(name.substring(1), value);
		else
			getAttributes().put(name, value);
	}

	protected Parameters getParameters()
	{
		return parameters;
	}
}
