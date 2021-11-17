package gate.tags;

import gate.util.Toolkit;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ParentTag extends SimpleTagSupport
{

	private Object source;
	private String target = "target";

	public void setSource(Object source)
	{
		this.source = Objects.requireNonNull(source);
	}

	public void setTarget(String target)
	{
		this.target = Objects.requireNonNull(target);
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		for (Object object : Toolkit.iterable(this.source))
		{
			getJspContext().setAttribute(this.target, object, PageContext.REQUEST_SCOPE);
			getJspBody().invoke(null);
			getJspContext().setAttribute(this.target, null, PageContext.REQUEST_SCOPE);
		}
	}

	public Object getSource()
	{
		return source;
	}
}
