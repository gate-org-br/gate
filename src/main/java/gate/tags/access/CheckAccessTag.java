package gate.tags.access;

import gate.annotation.Current;
import gate.entity.User;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class CheckAccessTag
		extends SimpleTagSupport
{

	@Inject
	@Current
	private User user;

	private String module;

	private String screen;

	private String action;

	private boolean strict;

	private AccessSecuredTag accessSecuredTag;
	private AccessBlockedTag accessBlockedTag;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getJspBody().invoke(null);

		if ("#".equals(module))
			module = ((PageContext) getJspContext()).getRequest().getParameter("MODULE");
		if ("#".equals(screen))
			screen = ((PageContext) getJspContext()).getRequest().getParameter("SCREEN");
		if ("#".equals(action))
			action = ((PageContext) getJspContext()).getRequest().getParameter("ACTION");

		if (user != null
				&& user.checkAccess(strict, module, screen, action))
		{
			if (accessSecuredTag != null)
				accessSecuredTag.invoke();
		} else if (accessBlockedTag != null)
			accessBlockedTag.invoke();

	}

	public void setModule(String module)
	{
		this.module = module;
	}

	public void setScreen(String screen)
	{
		this.screen = screen;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public void setStrict(boolean strict)
	{
		this.strict = strict;
	}

	void setAccessSecuredTag(AccessSecuredTag allowedTag)
	{
		this.accessSecuredTag = allowedTag;
	}

	void setAccessBlockedTag(AccessBlockedTag forbiddenTag)
	{
		this.accessBlockedTag = forbiddenTag;
	}
}
