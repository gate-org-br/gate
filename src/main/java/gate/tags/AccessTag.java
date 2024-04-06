package gate.tags;

import gate.Call;
import gate.annotation.Current;
import gate.entity.User;
import gate.error.BadRequestException;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

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
		try
		{
			call = Call.of(request, module, screen, action);
		} catch (BadRequestException ex)
		{
			throw new JspException(ex);
		}
	}

	public boolean checkAccess()
	{
		return call.checkAccess(user);
	}
}
