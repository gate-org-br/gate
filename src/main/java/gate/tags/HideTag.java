package gate.tags;

import gate.util.Icons;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class HideTag extends AttributeTag
{

	private String icon;
	private String name;

	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public void doTag() throws JspException, IOException
	{

		getAttributes().put("href", "#");
		getAttributes().put("target", "_hide");
		getJspContext().getOut().println("<a " + getAttributes() + ">");

		if (getJspBody() == null)
		{
			if (name != null)
				getJspContext().getOut().print(name);
			if (icon != null)
				getJspContext().getOut().print("<i>"
					+ Icons.getInstance().get(icon).orElse(Icons.getIcon("return"))
						.toString() + "</i>");
		} else
			getJspBody().invoke(null);

		getJspContext().getOut().print("</a>");
	}
}
