package gate.tags.link;

import gate.Gate;
import gate.annotation.Current;
import gate.base.Screen;
import gate.entity.User;
import gate.tags.AnchorTag;
import gate.util.Icons;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class ModuleLinkTag extends AnchorTag
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
		Class<Screen> type = Screen.getScreen(module, null).orElseThrow(() -> new JspException("Requisição inválida"));
		Method method = Screen.getAction(type, null).orElseThrow(() -> new JspException("Requisição inválida"));

		if (Gate.checkAccess(user, module, null, null, type, method))
		{
			getAttributes().putIfAbsent("tabindex", "2");
			getAttributes().putIfAbsent("title", "Retornar");
			getAttributes().put("href", "Gate?MODULE=" + module);

			getJspContext().getOut().println("<a " + getAttributes() + ">");
			if (getJspBody() == null)
				getJspContext().getOut().println("Retornar<i>"
					+ Icons.getInstance().get("return").orElse(Icons.UNKNOWN) + "</i>");
			else
				getJspBody().invoke(null);
			getJspContext().getOut().println("</a>");
		}
	}
}
