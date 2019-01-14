package gate.tags.template;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class InsertTag extends SimpleTagSupport
{

	@Override
	public void doTag() throws JspException, IOException
	{
		String body = (String) getJspContext()
			.getAttribute("body", PageContext.REQUEST_SCOPE);
		getJspContext().getOut().write(body);
		getJspContext().removeAttribute("body", PageContext.REQUEST_SCOPE);
		super.doTag();
	}
}
