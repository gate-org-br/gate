package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class SeparatorTag extends DynamicAttributeTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		try
		{
			PageContext pageContext = (PageContext) getJspContext();

			pageContext.getOut().print("");
			pageContext.getOut().print("<li class='Separator' " + getAttributes() + ">");
			pageContext.getOut().print("&nbsp;");
			pageContext.getOut().print("</li>");

		} catch (SecurityException ex)
		{
			throw new JspException(ex);
		}
	}
}
