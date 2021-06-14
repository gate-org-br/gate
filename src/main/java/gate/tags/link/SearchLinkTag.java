package gate.tags.link;

import gate.tags.DynamicAttributeTag;
import gate.util.Icons;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class SearchLinkTag extends DynamicAttributeTag
{

	@Inject
	private HttpServletRequest request;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		getAttributes().putIfAbsent("tabindex", "2");
		getAttributes().put("formaction", "Gate?" + request.getQueryString());

		getJspContext().getOut().println("<button " + getAttributes() + ">");
		if (getJspBody() == null)
			getJspContext().getOut().println("Pesquisar<i>"
				+ Icons.getInstance().get("commit").orElse(Icons.UNKNOWN) + "</i>");
		else
			getJspBody().invoke(null);
		getJspContext().getOut().println("</button>");

	}
}
