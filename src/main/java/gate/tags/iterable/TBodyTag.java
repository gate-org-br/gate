package gate.tags.iterable;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import java.io.IOException;

public class TBodyTag extends IterableTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (source != null)
		{
			boolean createIndex
				= getJspContext().getAttribute(index, PageContext.REQUEST_SCOPE)
				== null;

			boolean createDepth
				= getJspContext().getAttribute(depth, PageContext.REQUEST_SCOPE)
				== null;

			if (createIndex)
				getJspContext().setAttribute(index, -1, PageContext.REQUEST_SCOPE);

			if (createDepth)
				getJspContext().setAttribute(depth, -1, PageContext.REQUEST_SCOPE);

			getJspContext().getOut().print("<tbody>");
			iterate(source);
			getJspContext().getOut().print("</tbody>");

			if (createIndex)
				getJspContext().removeAttribute(index);

			if (createDepth)
				getJspContext().removeAttribute(depth);
		} else
			getJspBody().invoke(null);

	}
}
