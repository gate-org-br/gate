package gate.tags.table;

import gate.converter.Converter;
import gate.tags.DynamicAttributeTag;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class THTag extends DynamicAttributeTag
{

	private Object value;
	private String format;
	private String empty;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		getJspContext().getOut().print(getAttributes().isEmpty()
			? "<th>"
			: "<th " + getAttributes().toString() + " >");

		if (getJspBody() == null)
		{
			String string = format != null
				? Converter.toText(value, format)
				: Converter.toText(value);

			if (string.isEmpty() && empty != null)
				string = empty;

			string = string.replaceAll("\\n", "<br/>");
			getJspContext().getOut().print(string);
		} else
			getJspBody().invoke(null);

		getJspContext().getOut().print("</th>");
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