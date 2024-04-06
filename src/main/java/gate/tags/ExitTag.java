package gate.tags;

import gate.icon.Icons;
import java.io.IOException;
import jakarta.servlet.jsp.JspException;

public class ExitTag extends AttributeTag
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
		getJspContext().getOut().print("<a " + getAttributes().toString() + " href='Gate'>");

		if (getJspBody() == null)
		{
			if (name != null)
				getJspContext().getOut().print(name);
			if (icon != null)
				getJspContext().getOut().print("<i>"
					+ Icons.getInstance().get(icon).orElse(Icons.getIcon("exit"))
						.toString() + "</i>");
		} else
			getJspBody().invoke(null);

		getJspContext().getOut().print("</a>");
	}

}
