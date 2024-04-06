package gate.tags.property;

import gate.converter.Converter;
import java.io.IOException;
import jakarta.servlet.jsp.JspException;

public class DateTimeLocalTag extends PropertyTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (!getAttributes().containsKey("type"))
			getAttributes().put("type", "date");
		if (!getAttributes().containsKey("value"))
			getAttributes().put("value", getName().endsWith("[]") ? "" : Converter.toISOString(getValue()));
		getJspContext().getOut().print("<input " + getAttributes().toString() + " />");
	}
}
