package gate.tags.access;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class AllowedTag extends SimpleTagSupport
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (!(getParent() instanceof AccessTag))
			throw new JspException("The Allowed tag must be inside an Access tag");
		((AccessTag) getParent()).setAllowedTag(this);
	}

	public void invoke() throws JspException, IOException
	{
		getJspBody().invoke(null);
	}
}
