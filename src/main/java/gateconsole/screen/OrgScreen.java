package gateconsole.screen;

import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.Org;
import gate.error.AppException;
import gate.error.NotFoundException;

import gateconsole.contol.OrgControl;
import javax.inject.Inject;

@Icon("2006")
@Name("Organização")
public class OrgScreen extends Screen
{

	private Org form;

	@Inject
	private OrgControl control;

	public String call()
	{
		try
		{
			form = control.select();
			return "/WEB-INF/views/gateconsole/Org/View.jsp";
		} catch (NotFoundException ex)
		{
			return "/WEB-INF/views/gateconsole/Org/ViewUpdate.jsp";
		}
	}

	@Icon("2057")
	@Name("Alterar")
	public String callUpdate()
	{
		try
		{
			if (isGET())
			{
				form = control.select();
			} else if (isPOST() && getMessages().isEmpty())
			{
				new OrgControl().update(getForm());
				getRequest().getSession().getServletContext()
					.setAttribute("org", new OrgControl().select());
				return call();
			}
		} catch (AppException ex)
		{
			setMessages(ex.getMessages());
		}
		return "/WEB-INF/views/gateconsole/Org/ViewUpdate.jsp";
	}

	public Org getForm()
	{
		if (form == null)
			form = new Org();
		return form;
	}
}
