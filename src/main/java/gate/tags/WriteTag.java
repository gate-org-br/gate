package gate.tags;

import gate.converter.Converter;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

public class WriteTag extends SimpleTagSupport
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
		if (value != null)
			getJspContext().getOut().print(Converter.toString(value));
	}
}
