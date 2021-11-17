package gate.tags.anchor;

import gate.type.Attributes;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class MenuItemTag extends AnchorTag
{

	private boolean fixed = false;

	public void setFixed(boolean fixed)
	{
		this.fixed = fixed;
	}

	@Override
	public void get() throws JspException, IOException
	{
		getJspContext().getOut().print("<li " + getAttributes() + ">");
		Attributes atrributes = new Attributes();
		atrributes.put("href", getURL());
		if (tabindex != null)
			atrributes.put("tabindex", tabindex);
		if (target != null)
			atrributes.put("target", target);

		getJspContext().getOut().print("<a " + atrributes + ">");

		if (getJspBody() != null)
			getJspBody().invoke(null);
		else
			getJspContext().getOut().print(createBody());

		getJspContext().getOut().print("</a>");
		getJspContext().getOut().print("</li>");
	}

	@Override
	public void post() throws JspException, IOException
	{
		getJspContext().getOut().print("<li " + getAttributes() + ">");
		Attributes atrributes = new Attributes();
		atrributes.put("formaction", getURL());
		if (tabindex != null)
			atrributes.put("tabindex", tabindex);
		if (target != null)
			atrributes.put("formtarget", target);

		getJspContext().getOut().print("<button " + atrributes + ">");

		if (getJspBody() != null)
			getJspBody().invoke(null);
		else
			getJspContext().getOut().print(createBody());

		getJspContext().getOut().print("</button>");
		getJspContext().getOut().print("</li>");
	}

	@Override
	public void accessDenied() throws JspException, IOException
	{
		if (fixed)
		{
			getJspContext().getOut().print("<li " + getAttributes() + ">");

			Attributes atrributes = new Attributes();
			atrributes.put("href", "#");
			atrributes.put("data-disabled", "true");
			if (tabindex != null)
				atrributes.put("tabindex", tabindex);

			getJspContext().getOut().print("<a " + atrributes + ">");
			if (getJspBody() != null)
				getJspBody().invoke(null);
			else
				getJspContext().getOut().print(createBody());

			getJspContext().getOut().print("</a>");
			getJspContext().getOut().print("</li>");
		}
	}
}
