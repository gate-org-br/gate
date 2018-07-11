package gateconsole.screen;

import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.Role;
import gate.entity.User;
import gate.error.AppException;
import gateconsole.contol.RoleControl;
import gateconsole.contol.UserControl;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

@Name("Perfis")
@Icon("gate.entity.Role")
public class RoleScreen extends Screen
{

	private Role form;

	private Collection<Role> page;

	@Inject
	private RoleControl control;

	public String call()
	{
		if (getMessages().size() > 0)
			return "/WEB-INF/views/gateconsole/Role/View.jsp";
		page = control.search();
		return "/WEB-INF/views/gateconsole/Role/View.jsp";
	}

	public String callImport()
	{
		setPage(new RoleControl().getChildRoles(getForm()));
		return "/WEB-INF/views/gateconsole/Role/ViewImport.jsp";
	}

	@Icon("search")
	@Name("Pesquisar")
	public String callSearch()
	{
		if (getMessages().size() > 0)
			return "/WEB-INF/views/gateconsole/Role/ViewSearch.jsp";
		page = control.search();
		return "/WEB-INF/views/gateconsole/Role/ViewSearch.jsp";
	}

	@Icon("select")
	@Name("Detalhes")
	public String callSelect()
	{
		try
		{
			setForm(control.select(getForm().getId()));
			return "/WEB-INF/views/gateconsole/Role/ViewSelect.jsp";
		} catch (AppException e)
		{
			setMessages(e.getMessages());
			return call();
		}
	}

	@Icon("insert")
	@Name("Inserir")
	public String callInsert()
	{
		if (isPOST() && getMessages().isEmpty())
		{
			try
			{
				control.insert(getForm());
				return callSelect();
			} catch (AppException ex)
			{
				setMessages(ex.getMessages());
			}
		}

		return "/WEB-INF/views/gateconsole/Role/ViewInsert.jsp";
	}

	@Icon("update")
	@Name("Alterar")
	public String callUpdate()
	{
		if (isGET())
		{
			try
			{
				setForm(control.select(getForm().getId()));
			} catch (AppException e)
			{
				setMessages(e.getMessages());
				return call();
			}

		} else if (getMessages().isEmpty())
		{
			try
			{
				setForm(control.update(getForm()));
				return callSelect();
			} catch (AppException e)
			{
				setMessages(e.getMessages());
			}
		}

		return "/WEB-INF/views/gateconsole/Role/ViewUpdate.jsp";
	}

	@Icon("delete")
	@Name("Remover")
	public String callDelete()
	{
		try
		{
			control.delete(getForm());
			getMessages().add("O perfil foi removido com sucesso.");
			return "/WEB-INF/views/gateconsole/Role/ViewResult.jsp";
		} catch (AppException e)
		{
			setMessages(e.getMessages());
			return callSelect();
		}
	}

	public Role getForm()
	{
		if (form == null)
			form = new Role();
		return form;
	}

	public void setForm(Role form)
	{
		this.form = form;
	}

	public Collection<Role> getPage()
	{
		return page;
	}

	public void setPage(Collection<Role> page)
	{
		this.page = page;
	}

	public List<User> getUsers()
	{
		return new UserControl().search(new User().setRole(getForm()));
	}
}
