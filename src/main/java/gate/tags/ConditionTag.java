package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ConditionTag extends SimpleTagSupport
{

	private boolean condition;
	private String otherwise;

	public void setIf(boolean condition)
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
		if (condition)
			getJspBody().invoke(null);
		else if (otherwise != null)
			getJspContext().getOut().print("<div class='TEXT'><h1>" + otherwise + "</h1></div>");
	}
}
