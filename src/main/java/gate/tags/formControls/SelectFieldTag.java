package gate.tags.formControls;

import gate.base.Screen;
import gate.lang.property.Property;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class SelectFieldTag extends SelectTag
{

	private String label;
	private Integer size;

	@Override
	public void doTag() throws JspException, IOException
	{
		getJspContext().getOut().println(size != null
			? "<label data-size=" + size + ">" : "<label>");

		if (label == null)
		{
			Screen screen = (Screen) getJspContext().findAttribute("screen");
			String displayName = Property.getProperty(screen.getClass(),
				getProperty()).getDisplayName();
			if (displayName != null)
				getJspContext().getOut().println(displayName + ":");
		} else
			getJspContext().getOut().println(label + ":");

		getJspContext().getOut().println("<span>");

		super.doTag();

		if (getJspBody() != null)
			getJspBody().invoke(null);

		getJspContext().getOut().println("</span>");
		getJspContext().getOut().println("</label>");
	}

	public void setSize(Integer size)
	{
		this.size = size;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}
}
