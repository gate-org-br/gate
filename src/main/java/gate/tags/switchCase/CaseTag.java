package gate.tags.switchCase;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class CaseTag extends SimpleTagSupport
{

	private Object value;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (!(getParent() instanceof SwitchTag))
			throw new JspException("The Case tag must be inside a Switch tag");
		((SwitchTag) getParent()).getCaseTags().add(this);
	}

	public void invoke() throws JspException, IOException
	{
		getJspBody().invoke(null);
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}
}
