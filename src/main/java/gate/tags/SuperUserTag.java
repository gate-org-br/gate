package gate.tags;

import gate.annotation.Current;
import gate.entity.User;
import jakarta.inject.Inject;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

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
