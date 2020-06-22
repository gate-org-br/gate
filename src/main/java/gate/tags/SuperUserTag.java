package gate.tags;

import gate.annotation.Current;
import gate.entity.User;

import java.io.IOException;
import javax.inject.Inject;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class SuperUserTag extends SimpleTagSupport
{

	@Inject
	@Current
	private User user;

	@Override
	public void doTag() throws JspException, IOException
	{
		if (user != null && user.isSuperUser())
			getJspBody().invoke(null);
	}
}
