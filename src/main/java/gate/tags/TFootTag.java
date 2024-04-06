package gate.tags;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

public class TFootTag extends SimpleTagSupport
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		getJspContext().getOut().print("<tfoot>");
		getJspBody().invoke(null);
		getJspContext().getOut().print("</tfoot>");
	}
}
