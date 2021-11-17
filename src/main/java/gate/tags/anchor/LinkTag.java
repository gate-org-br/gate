package gate.tags.anchor;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public class LinkTag extends AnchorTag
{

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
			getJspContext().getOut().print(createBody());

		getJspContext().getOut().print("</a>");
	}

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
			getJspContext().getOut().print(createBody());

		getJspContext().getOut().print("</button>");
	}

	@Override
	public void accessDenied() throws JspException, IOException
	{

	}

	@Override
	public void otherwise() throws JspException, IOException
	{
		getJspContext().getOut().print(otherwise);
	}
}
