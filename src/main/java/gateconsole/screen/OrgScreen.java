package gateconsole.screen;

import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.Org;
import gate.error.AppException;
import gate.error.NotFoundException;
import gate.io.URL;
import gateconsole.contol.OrgControl;
import javax.inject.Inject;

@Icon("2006")
@Name("Organização")
public class OrgScreen extends Screen
{

	private Org form;

	@Inject
	private OrgControl control;

	public Object call()
	{
		try
		{
			form = control.select().orElseThrow(NotFoundException::new);
			return "/WEB-INF/views/gateconsole/Org/View.jsp";
		} catch (NotFoundException ex)
		{
			return new URL("Gate")
				.setModule(getModule())
				.setScreen(getScreen())
				.setAction("Update");
		}
	}

	@Icon("2057")
	@Name("Alterar")
	public String callUpdate()
	{
		form = control.select().orElse(new Org());
		return "/WEB-INF/views/gateconsole/Org/ViewUpdate.jsp";
	}

	@Icon("commit")
	@Name("Concluir")
	public Object callCommit()
	{
		try
		{
			if (!getMessages().isEmpty())
				throw new AppException(getMessages());

			control.update(getForm());
			getRequest().getSession().getServletContext()
				.setAttribute("org", control.select().orElseThrow(NotFoundException::new));

			return new URL("Gate")
				.setModule(getModule())
				.setScreen(getScreen());
		} catch (AppException ex)
		{
			return new URL("Gate")
				.setModule(getModule())
				.setScreen(getScreen())
				.setAction("Update")
				.setMessages(ex.getMessages());
		}
	}

	@Icon("dwload")
	public Object callDwload()
	{
		return control.dwload();
	}

	public Org getForm()
	{
		if (form == null)
			form = new Org();
		return form;
	}
}
