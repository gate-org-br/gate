package gate.tags.access;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class AccessSecuredTag extends SimpleTagSupport
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (!(getParent() instanceof CheckAccessTag))
			throw new JspException("The accessSecured tag must be inside a checkAccess tag");
		((CheckAccessTag) getParent()).setAccessSecuredTag(this);
	}

	public void invoke() throws JspException, IOException
	{
		getJspBody().invoke(null);
	}
}
