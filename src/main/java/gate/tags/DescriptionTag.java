package gate.tags;

import gate.annotation.Description;
import java.io.IOException;

import javax.servlet.jsp.JspException;

public class DescriptionTag extends DynamicAttributeTag
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
		getJspContext().getOut().print(Description.Extractor.extract(type).orElse("?"));
	}

}
