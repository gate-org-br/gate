package gate.tags.formControls;

import gate.tags.formControls.PropertyTag;
import gate.converter.Converter;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class FileTag extends PropertyTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getAttributes().put("type", "file");
		getAttributes().put("value", getName().endsWith("[]") ? "" : Converter.toString(getValue()));
		getJspContext().getOut().print(String.format("<input %s/>%n", getAttributes().toString()));
	}
}
