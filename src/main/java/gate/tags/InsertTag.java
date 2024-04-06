package gate.tags;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

public class InsertTag extends SimpleTagSupport
{

	@Override
	public void doTag() throws JspException, IOException
	{
		String body = (String) getJspContext().getAttribute("g-template-content", PageContext.REQUEST_SCOPE);
		getJspContext().getOut().write(body);
		getJspContext().removeAttribute("g-template-content", PageContext.REQUEST_SCOPE);
		super.doTag();
	}
}
