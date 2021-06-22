package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public class ColorTag extends AttributeTag
{

	private Object type;

	public void setType(Object type)
	{
		this.type = type;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getJspContext().getOut().print(TagLib.color(type));
	}

}
