package gate.tags.formControls;

import gate.converter.Converter;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class TextTag extends PropertyTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (!getAttributes().containsKey("type"))
			getAttributes().put("type", "text");
		if (!getAttributes().containsKey("value"))
			getAttributes().put("value", getName().endsWith("[]") ? "" : Converter.toString(getValue()));
		getJspContext().getOut().print("<input " + getAttributes().toString() + " />");
	}
}
