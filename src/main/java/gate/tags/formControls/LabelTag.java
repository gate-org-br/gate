package gate.tags.formControls;

import gate.converter.Converter;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class LabelTag extends PropertyTag
{

	private String empty;
	private String format;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		String string = format != null
			? Converter.toText(getValue(), format)
			: Converter.toText(getValue());
		if (string.isEmpty() && empty != null)
			string = empty;
		string = string.replaceAll("\\n", "<br/>");

		getJspContext().getOut().println("<label " + getAttributes() + ">" + string + "</label>");
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

	public void setEmpty(String emptyText)
	{
		this.empty = emptyText;
	}
}
