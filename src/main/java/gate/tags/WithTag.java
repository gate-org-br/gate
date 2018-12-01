package gate.tags;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class WithTag extends SimpleTagSupport
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
			if (this.type != null)
				this.value = Class.forName(type).getDeclaredConstructor().newInstance();

			getJspContext().setAttribute(name, value, PageContext.REQUEST_SCOPE);
			getJspBody().invoke(null);
			getJspContext().removeAttribute(name, PageContext.REQUEST_SCOPE);
		} catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException
				| JspException | NoSuchMethodException | SecurityException | IllegalArgumentException
				| InvocationTargetException ex)
		{
			throw new JspException(ex);
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
