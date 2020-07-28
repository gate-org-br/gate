package gate.tags;

import gate.type.Attributes;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class MenuTag extends AnchorTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (!getCondition()
			|| !checkAccess())
			return;

		PageContext pageContext = (PageContext) getJspContext();

		pageContext.getOut().print("<li " + getAttributes() + ">");

		Attributes attributes = new Attributes();
		if (getTabindex() != null)
			attributes.put("tabindex", getTabindex());
		if ("POST".equalsIgnoreCase(getMethod()))
		{
			attributes.put("formaction", getURL());
			if (getTarget() != null)
				attributes.put("formtarget", getTarget());

			pageContext.getOut().print("<button " + attributes + ">");
			pageContext.getOut().print(createBody());
			pageContext.getOut().print("</button>");
		} else
		{
			attributes.put("href", getURL());
			if (getTarget() != null)
				attributes.put("target", getTarget());

			pageContext.getOut().print("<a " + attributes + ">");
			pageContext.getOut().print(createBody());
			pageContext.getOut().print("</a>");
		}

		pageContext.getOut().print("<ul>");
		getJspBody().invoke(null);
		pageContext.getOut().print("</ul>");

		pageContext.getOut().print("</li>");
	}
}
