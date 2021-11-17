package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class LoginTag extends SimpleTagSupport
{

	private String module;
	private String screen;
	private String action;

	@Override
	public void doTag() throws JspException, IOException
	{
		getJspContext().getOut().println("<form method ='POST' target ='_top' action='Gate'>");
		if (module != null && !module.isEmpty())
			getJspContext().getOut().println(String.format("<input type='hidden' name='MODULE' value='%s'/>", module));
		if (screen != null && !screen.isEmpty())
			getJspContext().getOut().println(String.format("<input type='hidden' name='SCREEN' value='%s'/>", screen));
		if (action != null && !action.isEmpty())
			getJspContext().getOut().println(String.format("<input type='hidden' name='ACTION' value='%s'/>", action));

		getJspBody().invoke(null);
		getJspContext().getOut().println("</form>");

	}

	public void setModule(String module)
	{
		this.module = module;
	}

	public void setScreen(String screen)
	{
		this.screen = screen;
	}

	public void setAction(String action)
	{
		this.action = action;
	}
}
