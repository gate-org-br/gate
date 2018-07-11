package gate.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class UseTag extends SimpleTagSupport
{

	private String name;
	private String type;
	private Object value;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		try
		{
			if (value != null)
				getJspContext().setAttribute(name,
						value, PageContext.REQUEST_SCOPE);
			else if (type != null)
				getJspContext().setAttribute(name,
						Class.forName(type).newInstance(), PageContext.REQUEST_SCOPE);

			getJspContext().setAttribute(name, value, PageContext.REQUEST_SCOPE);
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e)
		{
			throw new JspException(e);
		}
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}
}
