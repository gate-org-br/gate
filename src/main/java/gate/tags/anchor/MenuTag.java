package gate.tags.anchor;

import gate.type.Attributes;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class MenuTag extends AnchorTag
{

	@Override
	public void get() throws JspException, IOException
	{
		getJspContext().getOut().print("<li " + getAttributes() + ">");

		Attributes attributes = new Attributes();
		if (tabindex != null)
			attributes.put("tabindex", tabindex);

		attributes.put("href", getURL());
		if (target != null)
			attributes.put("target", target);

		getJspContext().getOut().print("<a " + attributes + ">");
		getJspContext().getOut().print(createBody());
		getJspContext().getOut().print("</a>");

		getJspContext().getOut().print("<ul>");
		getJspBody().invoke(null);
		getJspContext().getOut().print("</ul>");

		getJspContext().getOut().print("</li>");

	}

	@Override
	public void post() throws JspException, IOException
	{
		getJspContext().getOut().print("<li " + getAttributes() + ">");

		Attributes attributes = new Attributes();
		if (tabindex != null)
			attributes.put("tabindex", tabindex);

		attributes.put("formaction", getURL());
		if (target != null)
			attributes.put("formtarget", target);

		getJspContext().getOut().print("<button " + attributes + ">");
		getJspContext().getOut().print(createBody());
		getJspContext().getOut().print("</button>");

		getJspContext().getOut().print("<ul>");
		getJspBody().invoke(null);
		getJspContext().getOut().print("</ul>");

		getJspContext().getOut().print("</li>");
	}
}
