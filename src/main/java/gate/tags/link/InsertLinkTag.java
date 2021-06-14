package gate.tags.link;

import gate.Gate;
import gate.annotation.Current;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.User;
import gate.tags.AnchorTag;
import gate.util.Icons;
import gate.util.Parameters;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class InsertLinkTag extends AnchorTag
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
		Method method = Screen.getAction(type, "Insert").orElseThrow(() -> new JspException("Requisição inválida"));

		if (Gate.checkAccess(user, module, screen, "Insert", type, method))
		{
			Parameters parameters = Parameters.parse(request.getQueryString());
			parameters.put("ACTION", "Insert");

			getAttributes().putIfAbsent("tabindex", "2");
			getAttributes().putIfAbsent("target", "_stack");
			getAttributes().putIfAbsent("title", Description.Extractor.extract(method)
				.or(() -> Name.Extractor.extract(method)).orElse("Inserir"));
			getAttributes().putIfAbsent("data-on-hide", "reload");
			getAttributes().put("href", "Gate?" + parameters.toString());

			getJspContext().getOut().println("<a " + getAttributes() + ">");
			if (getJspBody() == null)
				getJspContext().getOut().println(Name.Extractor.extract(method).orElse("Inserir") + "<i>"
					+ Icon.Extractor.extract(method).or(() -> Icons.getInstance().get("insert")).orElse(Icons.UNKNOWN) + "</i>");
			else
				getJspBody().invoke(null);
			getJspContext().getOut().println("</a>");
		}
	}
}
