package gate.tags;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ExceptionTag extends SimpleTagSupport
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		PageContext pc = (PageContext) getJspContext();

		if (pc.getRequest().getAttribute("exception") != null)
		{
			Throwable exception = (Throwable) pc.getRequest().getAttribute("exception");
			getJspContext().getOut().print("<div class='text'><pre>");
			exception.printStackTrace(new PrintWriter(getJspContext().getOut()));
			getJspContext().getOut().print("</pre></div>");
		}
	}
}
