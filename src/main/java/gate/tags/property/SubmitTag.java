package gate.tags.property;

import gate.converter.Converter;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class SubmitTag extends PropertyTag
{

	private Object value;

	public void setValue(Object value)
	{
		this.value = value;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getAttributes().put("type", "SUBMIT");
		getAttributes().put("value", Converter.toString(value));
		getJspContext().getOut().print(String.format("<input %s/>", getAttributes().toString()));
	}
}
