package gate.tags;

import gate.Gate;
import gate.entity.User;
import gate.util.Icons;

import java.io.IOException;
import javax.inject.Inject;

import javax.servlet.jsp.JspException;
import gate.annotation.Current;
import gate.io.URL;

public class DeskItemTag extends DynamicAttributeTag
{

	@Inject
	@Current
	private User user;

	private String module;

	private String screen;

	private String action;

	private String arguments;

	private Object icon;

	public void setIcon(Object icon)
	{
		this.icon = icon;
	}

	public String getArguments()
	{
		return arguments;
	}

	public void setArguments(String arguments)
	{
		this.arguments = arguments;
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

	@Override
	public void doTag() throws JspException, IOException
	{
		if (user != null && user.checkAccess(module, screen, action))
		{

			getJspContext().getOut().print(String.format("<li data-action='%s' %s>",
					URL.toString(module, screen, action, arguments), getAttributes().toString()));
			Icons.Icon icon = Icons.getInstance().get(this.icon, null);
			if (icon == null)
				getJspContext().getOut().print(String.format("<span %s>%s</span>", getAttributes().toString(), "??"));
			else if (icon.getCode().length() == 1)
				getJspContext().getOut().print(String.format("<span %s>%s</span>", getAttributes().toString(), icon
						.getCode()));
			else
				getJspContext().getOut().print(String.format("<i %s>&#x%s;</i>", getAttributes().toString(), icon
						.getCode()));
			getJspContext().getOut().print("<label>");
			getJspBody().invoke(null);
			getJspContext().getOut().print("</label>");
			getJspContext().getOut().print("</li>");
		}
	}
}
