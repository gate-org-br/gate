package gate.tags.link;

import gate.tags.DynamicAttributeTag;
import gate.util.Icons;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class CancelLinkTag extends DynamicAttributeTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		getAttributes().put("href", "#");
		getAttributes().put("target", "_hide");
		getAttributes().putIfAbsent("tabindex", "2");
		getAttributes().putIfAbsent("class", "Cancel");
		getAttributes().putIfAbsent("title", "Cancelar");

		getJspContext().getOut().println("<a " + getAttributes() + ">");
		if (getJspBody() == null)
			getJspContext().getOut().println("Cancelar<i>"
				+ Icons.getInstance().get("cancel").orElse(Icons.UNKNOWN) + "</i>");
		else
			getJspBody().invoke(null);
		getJspContext().getOut().println("</a>");

	}
}
