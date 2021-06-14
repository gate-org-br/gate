package gate.tags.link;

import gate.Gate;
import gate.annotation.Current;
import gate.base.Screen;
import gate.entity.User;
import gate.tags.DynamicAttributeTag;
import gate.util.Icons;
import gate.util.Parameters;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class SelectLinkTag extends DynamicAttributeTag
{

	@Inject
	@Current
	private User user;

	@Inject
	private HttpServletRequest request;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		String module = request.getParameter("MODULE");
		String screen = request.getParameter("SCREEN");
		Class<Screen> type = Screen.getScreen(module, screen).orElseThrow(() -> new JspException("Requisição inválida"));
		Method method = Screen.getAction(type, "Select").orElseThrow(() -> new JspException("Requisição inválida"));

		if (Gate.checkAccess(user, module, screen, "Select", type, method))
		{
			Parameters parameters = Parameters.parse(request.getQueryString());
			parameters.put("ACTION", "Select");

			getAttributes().putIfAbsent("class", "Cancel");
			getAttributes().putIfAbsent("title", "Cancelar");
			getAttributes().put("href", "Gate?" + parameters.toString());

			getJspContext().getOut().println("<a " + getAttributes() + ">");
			if (getJspBody() == null)
				getJspContext().getOut().println("Cancelar<i>"
					+ Icons.getInstance().get("cancel").orElse(Icons.UNKNOWN) + "</i>");
			else
				getJspBody().invoke(null);
			getJspContext().getOut().println("</a>");
		}
	}
}
