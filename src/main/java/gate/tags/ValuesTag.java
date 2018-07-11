package gate.tags;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ValuesTag extends SimpleTagSupport
{

	private String index;
	private String source;
	private String target;
	private Boolean reverse;

	public void setIndex(String index)
	{
		this.index = index;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public void setReverse(Boolean reverse)
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
			Object[] items = Class.forName(source).getEnumConstants();
			if (Boolean.TRUE.equals(reverse))
				Collections.reverse(Arrays.asList(items));
			for (Object item : items)
			{
				if (index != null)
					getJspContext().setAttribute(index, i++, PageContext.REQUEST_SCOPE);
				if (target != null)
					getJspContext().setAttribute(target, item, PageContext.REQUEST_SCOPE);
				getJspBody().invoke(null);
				if (target != null)
					getJspContext().setAttribute(target, null, PageContext.REQUEST_SCOPE);
				if (index != null)
					getJspContext().setAttribute(index, null, PageContext.REQUEST_SCOPE);

			}
		} catch (ClassNotFoundException e)
		{
			throw new JspException(e);
		}
	}
}
