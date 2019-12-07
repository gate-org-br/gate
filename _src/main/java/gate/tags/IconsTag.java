package gate.tags;

import gate.util.Icons;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class IconsTag extends SimpleTagSupport
{

	private String name;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		for (Icons.Icon icon : Icons.getInstance().get())
		{
			getJspContext().setAttribute(getName(), icon, PageContext.REQUEST_SCOPE);
			getJspBody().invoke(null);
			getJspContext().removeAttribute(getName(), PageContext.REQUEST_SCOPE);
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
