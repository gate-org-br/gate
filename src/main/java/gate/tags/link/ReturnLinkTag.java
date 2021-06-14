package gate.tags.link;

import gate.tags.DynamicAttributeTag;
import gate.util.Icons;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class ReturnLinkTag extends DynamicAttributeTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		getAttributes().put("href", "#");
		getAttributes().putIfAbsent("tabindex", "2");
		getAttributes().putIfAbsent("target", "_hide");
		getAttributes().putIfAbsent("title", "Retornar");

		getJspContext().getOut().println("<a " + getAttributes() + ">");
		if (getJspBody() == null)
			getJspContext().getOut().println("Retornar<i>"
				+ Icons.getInstance().get("return").orElse(Icons.UNKNOWN) + "</i>");
		else
			getJspBody().invoke(null);
		getJspContext().getOut().println("</a>");

	}
}
