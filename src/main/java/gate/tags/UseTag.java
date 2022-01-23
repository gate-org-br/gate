package gate.tags;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
					Thread.currentThread().getContextClassLoader().loadClass(type).getDeclaredConstructor()
						.newInstance(), PageContext.REQUEST_SCOPE);

			getJspContext().setAttribute(name, value, PageContext.REQUEST_SCOPE);
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException
			| NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex)
		{
			throw new IOException(ex);
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
