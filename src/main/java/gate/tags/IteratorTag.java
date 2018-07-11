package gate.tags;

import gate.lang.property.Property;
import gate.util.Toolkit;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class IteratorTag extends SimpleTagSupport
{

	private Object source;

	private String target;

	private String index;

	private String depth;

	private String children;

	public void setIndex(String index)
	{
		this.index = index;
	}

	public void setDepth(String depth)
	{
		this.depth = depth;
	}

	public void setChildren(String children)
	{
		this.children = children;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	public void setSource(Object source)
	{
		this.source = source;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (this.index != null
				&& getJspContext().getAttribute(this.index, PageContext.REQUEST_SCOPE) == null)
		{
			getJspContext().setAttribute(this.index, -1, PageContext.REQUEST_SCOPE);
			for (Object target : Toolkit.iterable(this.source))
				iterate(target, 0);
			getJspContext().setAttribute(this.index, null, PageContext.REQUEST_SCOPE);
			return;
		}

		for (Object target : Toolkit.iterable(this.source))
			iterate(target, 0);
	}

	public void iterate(Object target, int depth) throws JspException, IOException
	{
		if (this.index != null)
			getJspContext().setAttribute(this.index, ((Integer) getJspContext().getAttribute(this.index,
					PageContext.REQUEST_SCOPE)) + 1,
					PageContext.REQUEST_SCOPE);
		if (this.target != null)
			getJspContext().setAttribute(this.target, target, PageContext.REQUEST_SCOPE);
		if (this.depth != null)
			getJspContext().setAttribute(this.depth, depth, PageContext.REQUEST_SCOPE);
		getJspBody().invoke(null);
		if (this.target != null)
			getJspContext().removeAttribute(this.target, PageContext.REQUEST_SCOPE);
		if (this.depth != null)
			getJspContext().removeAttribute(this.depth, PageContext.REQUEST_SCOPE);

		if (target != null && this.children != null)
			for (Object child : Toolkit.iterable(Property.getProperty(target.getClass(), this.children).getValue(target)))
				iterate(child, depth + 1);
	}
}
