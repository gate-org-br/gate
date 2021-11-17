package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class WhenTag extends SimpleTagSupport
{

	private boolean condition;

	public boolean isCondition()
	{
		return condition;
	}

	public void setCondition(boolean condition)
	{
		this.condition = condition;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (!(getParent() instanceof ChooseTag))
			throw new IOException("The When tag must be inside a Choose tag");
		((ChooseTag) getParent()).getWhenTags().add(this);
	}

	public void invoke() throws JspException, IOException
	{
		getJspBody().invoke(null);
	}
}
