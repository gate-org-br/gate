package gate.tags.iterable;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import java.io.IOException;

public class IteratorTag extends IterableTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
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

		iterate(source);

		if (createIndex)
			getJspContext().removeAttribute(index);

		if (createDepth)
			getJspContext().removeAttribute(depth);

	}

}
