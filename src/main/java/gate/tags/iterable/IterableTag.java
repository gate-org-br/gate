package gate.tags.iterable;

import gate.util.Toolkit;
import java.io.IOException;
import java.util.Objects;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.LambdaExpression;
import javax.el.StandardELContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public abstract class IterableTag extends SimpleTagSupport
{

	protected Object source;
	protected String target = "target";
	protected String index = "index";
	protected String depth = "depth";
	protected LambdaExpression children;
	protected final ELContext EL_CONTEXT = new StandardELContext(ExpressionFactory.newInstance());

	public void setSource(Object source)
	{
		this.source = Objects.requireNonNull(source);
	}

	public void setTarget(String target)
	{
		this.target = Objects.requireNonNull(target);
	}

	public void setIndex(String index)
	{
		this.index = Objects.requireNonNull(index);
	}

	public void setDepth(String depth)
	{
		this.depth = Objects.requireNonNull(depth);
	}

	public void setChildren(LambdaExpression children)
	{
		this.children = children;
	}

	protected void iterate(Object iterable) throws JspException, IOException
	{
		increment(depth);
		for (Object object : Toolkit.iterable(iterable))
		{
			increment(index);
			if (this.target != null)
				getJspContext().setAttribute(this.target, object, PageContext.REQUEST_SCOPE);
			getJspBody().invoke(null);
			if (this.target != null)
				getJspContext().removeAttribute(this.target, PageContext.REQUEST_SCOPE);
			if (object != null && this.children != null)
				for (Object child : Toolkit.iterable(children.invoke(EL_CONTEXT, object)))
					iterate(child);
		}
		decrement(depth);
	}

	protected void increment(String field)
	{
		Integer value = (Integer) getJspContext().getAttribute(field, PageContext.REQUEST_SCOPE);
		value++;
		getJspContext().setAttribute(field, value, PageContext.REQUEST_SCOPE);
	}

	protected void decrement(String field)
	{
		Integer value = (Integer) getJspContext().getAttribute(field, PageContext.REQUEST_SCOPE);
		value--;
		getJspContext().setAttribute(field, value, PageContext.REQUEST_SCOPE);
	}

}
