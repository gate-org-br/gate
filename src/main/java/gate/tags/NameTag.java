package gate.tags;

import gate.annotation.Name;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class NameTag extends DynamicAttributeTag
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
		getJspContext().getOut().print(Name.Extractor.extract(type).orElse("?"));
	}

}
