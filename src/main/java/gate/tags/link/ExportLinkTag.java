package gate.tags.link;

import gate.Gate;
import gate.annotation.Current;
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

public class ExportLinkTag extends AnchorTag
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
		Method method = Screen.getAction(type, "Export").orElseThrow(() -> new JspException("Requisição inválida"));

		if (Gate.checkAccess(user, module, screen, "Export", type, method))
		{
			Parameters parameters = Parameters.parse(request.getQueryString());
			parameters.put("ACTION", "Export");
			getAttributes().put("target", "_report");
			getAttributes().putIfAbsent("tabindex", "2");
			getAttributes().putIfAbsent("title", "Imprimir");
			getAttributes().put("href", "Gate?" + parameters.toString());

			getJspContext().getOut().println("<a " + getAttributes() + ">");
			if (getJspBody() == null)
				getJspContext().getOut().println(Name.Extractor.extract(method).orElse("Imprimir") + "<i>"
					+ Icon.Extractor.extract(method).or(() -> Icons.getInstance().get("report")).orElse(Icons.UNKNOWN) + "</i>");
			else
				getJspBody().invoke(null);
			getJspContext().getOut().println("</a>");
		}
	}
}
