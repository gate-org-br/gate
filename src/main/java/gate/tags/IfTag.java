package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class IfTag extends SimpleTagSupport
{

	private boolean condition;
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
		if (condition)
			getJspBody().invoke(null);
		else if (otherwise != null)
			getJspContext().getOut().print(otherwise);
	}
}
