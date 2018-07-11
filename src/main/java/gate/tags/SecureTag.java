package gate.tags;

import gate.entity.User;

import java.io.IOException;
import javax.inject.Inject;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import gate.annotation.Current;
import java.util.regex.Pattern;

public class SecureTag extends SimpleTagSupport
{

	@Inject
	@Current
	private User user;

	private String module;

	private String screen;

	private String action;

	private boolean strict;

	private String otherwise;

	private static final String[] DEFAULT = new String[1];
	private static final Pattern SPLIT = Pattern.compile("[ ]*,[ ]*");

	public void setOtherwise(String otherwise)
	{
		this.otherwise = otherwise;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public void setModule(String module)
	{
		this.module = module;
	}

	public void setScreen(String screen)
	{
		this.screen = screen;
	}

	public void setStrict(boolean strict)
	{
		this.strict = strict;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		if ("#".equals(module))
			module = ((PageContext) getJspContext()).getRequest().getParameter("MODULE");
		if ("#".equals(screen))
			screen = ((PageContext) getJspContext()).getRequest().getParameter("SCREEN");
		if ("#".equals(action))
			action = ((PageContext) getJspContext()).getRequest().getParameter("ACTION");

		if (user != null)
			for (String module : this.module != null ? SPLIT.split(this.module) : DEFAULT)
				for (String screen : this.screen != null ? SPLIT.split(this.screen) : DEFAULT)
					for (String action : this.action != null ? SPLIT.split(this.action) : DEFAULT)
						if (user.checkAccess(strict, module, screen, action))
						{
							getJspBody().invoke(null);
							return;
						}

		if (otherwise != null)
			getJspContext().getOut().print(otherwise);
	}
}
