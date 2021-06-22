package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public class HideTag extends AttributeTag
{

	private String type;

	public void setType(String type)
	{
		this.type = type;
	}

	@Override
	public void doTag() throws JspException, IOException
	{

		getAttributes().put("href", "#");
		getAttributes().put("target", "_hide");
		getJspContext().getOut().println("<a " + getAttributes() + ">");
		getJspBody().invoke(null);
		getJspContext().getOut().println("</a>");
	}
}
