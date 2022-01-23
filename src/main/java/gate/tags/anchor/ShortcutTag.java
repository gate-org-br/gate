package gate.tags.anchor;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public class ShortcutTag extends AnchorTag
{

	@Override
	public void post() throws JspException, IOException
	{

		if (tabindex != null)
			getAttributes().put("tabindex", tabindex);

		getAttributes().put("formaction", getURL());

		if (target != null)
			getAttributes().put("formtarget", target);

		getJspContext().getOut().print("<button " + getAttributes() + ">");

		if (getJspBody() != null)
			getJspBody().invoke(null);
		else
			getJspContext().getOut().print(getIcon());

		getJspContext().getOut().print("</button>");
	}

	@Override
	public void get() throws JspException, IOException
	{
		if (tabindex != null)
			getAttributes().put("tabindex", tabindex);

		getAttributes().put("href", getURL());
		if (target != null)
			getAttributes().put("target", target);

		getJspContext().getOut().print("<a " + getAttributes() + ">");

		if (getJspBody() != null)
			getJspBody().invoke(null);
		else
			getJspContext().getOut().print(getIcon());

		getJspContext().getOut().print("</a>");
	}

	private String getIcon()
	{
		return call.getIcon().map(e -> "<i>" + e + "</i>")
			.or(() -> call.getEmoji().map(e -> "<e>" + e + "</e>"))
			.orElse("?");
	}
}
