package gateconsole.screen;

import gate.annotation.CopyIcon;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.Func;
import gate.entity.Role;
import gate.entity.User;
import gate.error.AppException;
import gate.util.Page;
import gateconsole.contol.FuncControl;
import gateconsole.contol.RoleControl;
import gateconsole.contol.UserControl;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

@Name("Perfis")
@CopyIcon(Role.class)
public class RoleScreen extends Screen
{

	private Role form;

	private Collection<Role> page;

	@Inject
	private RoleControl control;

	public String call()
	{
		try
		{
			page = control.search();
		} catch (AppException ex)
		{
			setMessages(ex.getMessages());
		}
		return "/WEB-INF/views/gateconsole/Role/View.jsp";
	}

	@Name("Sub Perfis")
	@CopyIcon(Role.class)
	public String callImport()
	{
		setPage(control.getChildRoles(getForm().getRole()));
		return "/WEB-INF/views/gateconsole/Role/ViewImport.jsp";
	}

	@Icon("search")
	@Name("Pesquisar")
	public String callSearch()
	{
		try
		{
			page = control.search();
		} catch (AppException ex)
		{
			setMessages(ex.getMessages());
		}
		return "/WEB-INF/views/gateconsole/Role/ViewSearch.jsp";
	}

	@Icon("select")
	@Name("Detalhe")
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

	@Name("Novo")
	@Icon("insert")
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
				control.update(getForm());
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
		} catch (AppException ex)
		{
			setMessages(ex.getMessages());
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

	@Name("Funções")
	@CopyIcon(Func.class)
	public static class FuncScreen extends Screen
	{

		private Func func;
		private Role role;
		private Page<Func> page;

		@Inject
		private FuncControl funcControl;

		@Inject
		private FuncControl.RoleControl control;

		public String call()
		{

			page = paginate(ordenate(control.search(role)));
			return "/WEB-INF/views/gateconsole/Role/Func/View.jsp";
		}

		@Icon("insert")
		@Name("Adcionar")
		public String callInsert()
		{

			try
			{
				control.insert(func, role);
				func = null;
			} catch (AppException ex)
			{
				setMessages(ex.getMessages());
			}
			return call();
		}

		@Icon("delete")
		@Name("Remover")
		public String callDelete()
		{

			try
			{
				control.delete(func, role);
				func = null;
			} catch (AppException ex)
			{
				setMessages(ex.getMessages());
			}
			return call();
		}

		public Role getRole()
		{
			if (role == null)
				role = new Role();
			return role;
		}

		public Func getFunc()
		{
			if (func == null)
				func = new Func();
			return func;
		}

		public Page<Func> getPage()
		{
			return page;
		}

		public List<Func> getFuncs()
		{
			return funcControl.search();
		}
	}
}
