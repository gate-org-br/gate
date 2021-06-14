package gate.tags;

import javax.servlet.jsp.tagext.DynamicAttributes;

public class DynamicAttributeTag extends AttributeTag implements DynamicAttributes
{

	@Override
	public void setDynamicAttribute(String uri, String name, Object value)
	{
		getAttributes().put(name, value);
	}
}
