package gate.tags.anchor;

import gate.util.Toolkit;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class TRTag extends AnchorTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		if (Toolkit.isEmpty(method, target, module, screen, action))
		{
			getJspContext().getOut().print("<tr " + getAttributes() + " >");
			getJspBody().invoke(null);
			getJspContext().getOut().print("</tr>");
		} else
			super.doTag();

	}

	@Override
	public void get() throws JspException, IOException
	{
		if (tabindex != null)
			getAttributes().put("tabindex", tabindex);
		getAttributes().put("data-action", getURL());
		if (target != null)
			getAttributes().put("data-target", target);
		getJspContext().getOut().print("<tr " + getAttributes() + " >");

		getJspBody().invoke(null);
		getJspContext().getOut().print("</tr>");
	}

	@Override
	public void post() throws JspException, IOException
	{
		if (tabindex != null)
			getAttributes().put("tabindex", tabindex);
		getAttributes().put("data-action", getURL());
		getAttributes().put("data-method", "post");
		if (target != null)
			getAttributes().put("data-target", target);

		getJspContext().getOut().print(getAttributes().isEmpty() ? "<tr>" : "<tr " + getAttributes() + " >");
		getJspBody().invoke(null);
		getJspContext().getOut().print("</tr>");
	}

	@Override
	public void accessDenied() throws JspException, IOException
	{
		getJspContext().getOut().print(getAttributes().isEmpty() ? "<tr>" : "<tr " + getAttributes() + " >");
		getJspBody().invoke(null);
		getJspContext().getOut().print("</tr>");
	}
}
