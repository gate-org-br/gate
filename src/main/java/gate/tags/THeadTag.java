package gate.tags;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

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
