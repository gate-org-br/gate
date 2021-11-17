package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class THeadTag extends SimpleTagSupport
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		getJspContext().getOut().print("<thead>");
		getJspBody().invoke(null);
		getJspContext().getOut().print("</thead>");
	}
}
