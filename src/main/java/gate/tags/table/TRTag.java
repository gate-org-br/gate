package gate.tags.table;

import gate.tags.*;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class TRTag extends AnchorTag
{

	@Override
	public void get() throws JspException, IOException
	{
		if (tabindex != null)
			getAttributes().put("tabindex", tabindex);
		getAttributes().put("data-action", getURL());
		if (target != null)
			getAttributes().put("data-target", target);

		getJspContext().getOut().print(getAttributes().isEmpty() ? "<tr>" : "<tr " + getAttributes().toString() + " >");
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

		getJspContext().getOut().print(getAttributes().isEmpty() ? "<tr>" : "<tr " + getAttributes().toString() + " >");
		getJspBody().invoke(null);
		getJspContext().getOut().print("</tr>");
	}

	@Override
	public void accessDenied() throws JspException, IOException
	{
		getJspContext().getOut().print(getAttributes().isEmpty() ? "<tr>" : "<tr " + getAttributes().toString() + " >");
		getJspBody().invoke(null);
		getJspContext().getOut().print("</tr>");
	}
}
