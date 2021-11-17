package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

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
