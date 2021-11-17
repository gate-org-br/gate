package gate.tags.property;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public class TextAreaTag extends PropertyTag
{

	@Override
	public void doTag() throws JspException, IOException
	{

		super.doTag();
		String value = getName().endsWith("[]") ? "" : getConverter().toString(getType(), getValue());
		getJspContext().getOut().print(String.format("<textarea %s>%s</textarea>", getAttributes().toString(), value));

	}
}
