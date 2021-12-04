package gate.tags;

import gate.annotation.Current;
import gate.entity.User;
import gate.Call;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public abstract class AccessTag extends SimpleTagSupport
{

	@Inject
	@Current
	private User user;

	private String module;

	private String screen;

	private String action;

	protected Call call;

	@Inject
	private HttpServletRequest request;

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

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		call = Call.of(request, module, screen, action);
	}

	public boolean checkAccess()
	{
		return call.checkAccess(user);
	}
}
