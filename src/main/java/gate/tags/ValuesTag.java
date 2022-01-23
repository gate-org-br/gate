package gate.tags;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ValuesTag extends SimpleTagSupport
{

	private String source;
	private String index = "index";
	private boolean reverse = false;
	private String target = "target";

	public void setSource(String source)
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

	public void setReverse(boolean reverse)
	{
		this.reverse = reverse;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		try
		{
			super.doTag();
			int i = 0;
			Object[] items = Thread.currentThread().getContextClassLoader().loadClass(source).getEnumConstants();
			if (reverse)
				Collections.reverse(Arrays.asList(items));
			for (Object item : items)
			{
				getJspContext().setAttribute(index, i++, PageContext.REQUEST_SCOPE);
				getJspContext().setAttribute(target, item, PageContext.REQUEST_SCOPE);
				getJspBody().invoke(null);
				getJspContext().removeAttribute(target, PageContext.REQUEST_SCOPE);
				getJspContext().removeAttribute(index, PageContext.REQUEST_SCOPE);
			}
		} catch (ClassNotFoundException ex)
		{
			throw new IOException(ex);
		}
	}
}
