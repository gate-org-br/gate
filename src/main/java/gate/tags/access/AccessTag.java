package gate.tags.access;

import gate.annotation.Current;
import gate.entity.User;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class AccessTag
		extends SimpleTagSupport
{

	@Inject
	@Current
	private User user;

	private String module;

	private String screen;

	private String action;

	private boolean strict;

	private AllowedTag allowedTag;
	private ForbiddenTag forbiddenTag;

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
			if (allowedTag != null)
				allowedTag.invoke();
		} else if (forbiddenTag != null)
			forbiddenTag.invoke();

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

	void setAllowedTag(AllowedTag allowedTag)
	{
		this.allowedTag = allowedTag;
	}

	void setForbiddenTag(ForbiddenTag forbiddenTag)
	{
		this.forbiddenTag = forbiddenTag;
	}
}
