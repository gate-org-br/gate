package gate.tags.switchCase;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class DefaultTag extends SimpleTagSupport
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (!(getParent() instanceof SwitchTag))
			throw new JspException("The Default tag must be inside a Switch tag");

		if (((SwitchTag) getParent()).getDefaultTag() != null)
			throw new JspException("Multiple Default tags found on Switch tag");

		((SwitchTag) getParent()).setDefaultTag(this);
	}

	public void invoke() throws JspException, IOException
	{
		getJspBody().invoke(null);
	}
}
