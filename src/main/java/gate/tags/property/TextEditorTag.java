package gate.tags.property;

import gate.converter.Converter;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class TextEditorTag extends PropertyTag
{
	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (!getAttributes().containsKey("value"))
			getAttributes().put("value", getName().endsWith("[]") ? "" : Converter.toString(getValue()));
		getJspContext().getOut().print("<g-text-editor " + getAttributes().toString() + "></g-text-editor>");
	}
}
