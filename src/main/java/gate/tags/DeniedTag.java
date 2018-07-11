package gate.tags;

import gate.entity.User;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class DeniedTag extends SimpleTagSupport
{

	private String module;

	private String screen;

	private String action;

	private String otherwise;

	public String getOtherwise()
	{
		return otherwise;
	}

	public void setOtherwise(String otherwise)
	{
		this.otherwise = otherwise;
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getModule()
	{
		return module;
	}

	public void setModule(String module)
	{
		this.module = module;
	}

	public String getScreen()
	{
		return screen;
	}

	public void setScreen(String screen)
	{
		this.screen = screen;
	}

	public void doTag() throws JspException, IOException
	{
		User user = (User) getJspContext().getAttribute("user", PageContext.SESSION_SCOPE);
		if (user == null || !user.checkAccess(module, screen, action))
			getJspBody().invoke(null);
		else if (getOtherwise() != null)
			getJspContext().getOut().print(getOtherwise());

	}
}
