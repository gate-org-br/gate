package gate.tags;

import gate.type.Attributes;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class AttributeTag extends SimpleTagSupport implements DynamicAttributes
{

	private final Attributes attributes = new Attributes();

	@Override
	public void setDynamicAttribute(String uri, String name, Object value)
	{
		getAttributes().put(name, value);
	}

	protected Attributes getAttributes()
	{
		return attributes;
	}

}
