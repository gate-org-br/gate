package gate.tags;

import gate.annotation.Current;
import gate.entity.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;

public class SuperUserTag extends SimpleTagSupport
{

	@Inject
	@Current
	@RequestScoped
	private User user;

	@Override
	public void doTag() throws JspException, IOException
	{
		if (user.isSuperUser())
			getJspBody().invoke(null);
	}
}
