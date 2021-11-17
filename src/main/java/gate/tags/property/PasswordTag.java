package gate.tags.property;

import gate.converter.Converter;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class PasswordTag extends PropertyTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (!getAttributes().containsKey("type"))
			getAttributes().put("type", "password");
		if (!getAttributes().containsKey("value"))
			getAttributes().put("value", getName().endsWith("[]") ? "" : Converter.toString(getValue()));
		getJspContext().getOut().print(String.format("<input %s/>", getAttributes().toString()));
	}
}
