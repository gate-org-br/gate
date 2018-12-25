package gateconsole.screen;

import gate.annotation.Current;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.Auth;
import gate.error.AppException;

import java.util.ArrayList;
import java.util.Collection;

import gate.entity.App;
import gateconsole.contol.AuthControl;
import javax.inject.Inject;

@Icon("2002")
@Name("Acessos")
public class AuthScreen extends Screen
{

	private Auth form;
	private Collection<Auth> page;

	@Inject
	@Current
	private Collection<App> apps;

	@Inject
	private AuthControl control;

	@Name("Acessos")
	@Icon("gate.entity.Auth")
	public String call() throws AppException
	{
		setPage(control.search(getForm()));
		return "/WEB-INF/views/gateconsole/Auth/View.jsp";
	}

	@Icon("search")
	@Name("Pesquisar")
	@Description("Pesquisar acessos")
	public String callSearch() throws AppException
	{
		return "/WEB-INF/views/gateconsole/Auth/ViewSearch.jsp";
	}

	@Icon("1002")
	@Name("Novo")
	public String callInsert() throws AppException
	{
		try
		{
			control.insert(getForm());
		} catch (AppException e)
		{
			setMessages(e.getMessages());
		}
		return call();
	}

	@Icon("2026")
	@Name("Remover")
	public String callDelete() throws AppException
	{
		try
		{
			control.delete(getForm());
		} catch (AppException e)
		{
			setMessages(e.getMessages());
		}
		return call();
	}

	public Auth getForm()
	{
		if (form == null)
			form = new Auth();
		return form;
	}

	public void setForm(Auth form)
	{
		this.form = form;
	}

	public Collection<Auth> getPage()
	{
		if (page == null)
			page = new ArrayList<>();
		return page;
	}

	public void setPage(Collection<Auth> page)
	{
		this.page = page;
	}

	public Collection<App> getApps()
	{
		return apps;
	}
}
