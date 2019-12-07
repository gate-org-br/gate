package gate.tags;

import gate.type.Attributes;

import javax.servlet.jsp.tagext.SimpleTagSupport;

public class AttributeTag extends SimpleTagSupport
{

	private final Attributes attributes = new Attributes();

	protected Attributes getAttributes()
	{
		return attributes;
	}
}
