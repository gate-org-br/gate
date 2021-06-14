package gate.tags.link;

import com.google.common.base.Optional;
import gate.Gate;
import gate.annotation.Color;
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

public class DeleteLinkTag extends AnchorTag
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
		Method method = Screen.getAction(type, "Delete").orElseThrow(() -> new JspException("Requisição inválida"));

		if (Gate.checkAccess(user, module, screen, "Delete", type, method))
		{
			Parameters parameters = Parameters.parse(request.getQueryString());
			parameters.put("ACTION", "Delete");

			getAttributes().putIfAbsent("tabindex", "2");
			getAttributes().putIfAbsent("title", Description.Extractor.extract(method)
				.or(() -> Name.Extractor.extract(method)).orElse("Remover"));
			getAttributes().putIfAbsent("style", "color: " + Color.Extractor.extract(method).orElse("#660000"));
			getAttributes().put("href", "Gate?" + parameters.toString());
			getAttributes().putIfAbsent("data-confirm", "Tem certeza de que deseja remover este registro?");

			getJspContext().getOut().println("<a " + getAttributes() + ">");
			if (getJspBody() == null)
				getJspContext().getOut().println(Name.Extractor.extract(method).orElse("Remover") + "<i>"
					+ Icon.Extractor.extract(method).or(() -> Icons.getInstance().get("delete")).orElse(Icons.UNKNOWN) + "</i>");
			else
				getJspBody().invoke(null);
			getJspContext().getOut().println("</a>");
		}

	}
}
