package gate.tags.iterable;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

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
