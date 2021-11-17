package gate.tags;

import gate.tags.*;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class TableTag extends AttributeTag
{

	private boolean condition = true;
	private String otherwise;

	public void setCondition(boolean condition)
	{
		this.condition = condition;
	}

	public void setOtherwise(String otherwise)
	{
		this.otherwise = otherwise;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (condition)
		{
			getJspContext().getOut().print(getAttributes().isEmpty()
				? "<table>"
				: "<table " + getAttributes().toString() + " >");
			getJspBody().invoke(null);
			getJspContext().getOut().print("</table>");
		} else if (otherwise != null)
		{
			getJspContext().getOut().print("<div class='TEXT'>");
			getJspContext().getOut().print("<h1>" + otherwise + "</h1>");
			getJspContext().getOut().print("</div>");
		}
	}

}
