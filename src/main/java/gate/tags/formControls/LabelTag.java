package gate.tags.formControls;

import gate.converter.Converter;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class LabelTag extends PropertyTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getJspContext().getOut().print(Converter.toText(getValue()));
	}
}
