package gate.tags;

import gate.converter.Converter;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class PrintTag extends SimpleTagSupport
{

	private Object value;
	private String format;
	private String empty;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		String string = format != null ? Converter.toText(value, format) : Converter.toText(value);
		if (string.isEmpty() && empty != null)
			string = empty;
		string = string.replaceAll("\\n", "<br/>");

		getJspContext().getOut().print(string);
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public void setEmpty(String emptyText)
	{
		this.empty = emptyText;
	}
}
