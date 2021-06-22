package gate.tags;

import gate.util.Parameters;
import javax.servlet.jsp.tagext.DynamicAttributes;

public class ParameterTag extends AttributeTag implements DynamicAttributes
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
