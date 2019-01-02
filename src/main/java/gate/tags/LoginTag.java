package gate.tags;

import gate.annotation.RetainRequestParameters;
import gate.base.Screen;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class LoginTag extends SimpleTagSupport
{

	private String module;
	private String screen;
	private String action;

	@Inject
	private HttpServletRequest request;

	@Override
	public void doTag() throws JspException, IOException
	{
		if (Screen.getScreen(module, screen).orElseThrow(() -> new IOException("Invalid request"))
			.isAnnotationPresent(RetainRequestParameters.class)
			|| Screen.getAction(module, action, action).orElseThrow(() -> new IOException("Invalid request"))
				.isAnnotationPresent(RetainRequestParameters.class))
		{
			getJspContext().getOut().println("<form method ='POST' action='Gate'>");
			for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet())
				for (String value : entry.getValue())
					getJspContext().getOut().println(String.format("<input type='hidden' name='%s' value='%s'/>",
						entry.getKey(), value));
		} else
		{
			getJspContext().getOut().println("<form method ='POST' target ='_top' action='Gate'>");
			if (module != null && !module.isEmpty())
				getJspContext().getOut().println(String.format("<input type='hidden' name='MODULE' value='%s'/>", module));
			if (screen != null && !screen.isEmpty())
				getJspContext().getOut().println(String.format("<input type='hidden' name='SCREEN' value='%s'/>", screen));
			if (action != null && !action.isEmpty())
				getJspContext().getOut().println(String.format("<input type='hidden' name='ACTION' value='%s'/>", action));

		}

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
