package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public class BlockTag extends AttributeTag
{

	private boolean condition;
	private String otherwise;

	public void setCondition(boolean condition)
	{
		this.condition = condition;
	}

	public void setOtherwise(String otherwise)
	{
		this.otherwise = otherwise;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (condition)
		{
			getJspBody().invoke(null);
		} else if (otherwise != null)
		{
			if (!getAttributes().containsKey("class"))
				getAttributes().put("class", "TEXT");
			getJspContext().getOut().print("<g-callout " + getAttributes() + ">");
			getJspContext().getOut().print(otherwise);
			getJspContext().getOut().print("</g-callout>");
		}
	}
}
