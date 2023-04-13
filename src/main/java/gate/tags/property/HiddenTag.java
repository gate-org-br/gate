package gate.tags.property;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public class HiddenTag extends PropertyTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (!getAttributes().containsKey("type"))
			getAttributes().put("type", "hidden");
		if (!getAttributes().containsKey("value"))
			getAttributes().put("value", getName().endsWith("[]") ? "" : getConverter().toString(getType(), getValue()));
		getJspContext().getOut().print(String.format("<input %s/>", getAttributes().toString()));
	}
}
