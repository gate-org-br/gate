package gate.tags;

import gate.base.Screen;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class BrowserTag extends SimpleTagSupport
{

	private String name;

	public void doTag() throws JspException, IOException
	{
		Screen screen = (Screen) getJspContext().findAttribute("screen");
		String browser = screen.getRequest().getHeader("User-Agent");
		if (browser != null && browser.toUpperCase().contains(getName().toUpperCase()))
			getJspBody().invoke(null);
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
