package gateconsole.screen;

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
import java.util.List;
import javax.inject.Inject;

@Name("Funções")
@Icon("gate.entity.Func")
public class FuncScreen extends Screen
{

	private Func form;
	private List<Func> page;

	@Inject
	private FuncControl control;

	public String call()
	{
		page = control.search(getForm());
		return "/WEB-INF/views/gateconsole/Func/View.jsp";
	}

	@Icon("select")
	@Name("Detalhes")
	public String callSelect()
	{
		try
		{
			form = control.select(getForm().getId());
			return "/WEB-INF/views/gateconsole/Func/ViewSelect.jsp";
		} catch (AppException e)
		{
			setMessages(e.getMessages());
			return call();
		}
	}

	@Name("Nova")
	@Icon("insert")
	public String callInsert()
	{
		if (isPOST()
			&& getMessages().isEmpty())
			try
			{
				control.insert(getForm());
				return callSelect();
			} catch (AppException e)
			{
				setMessages(e.getMessages());
			}
		return "/WEB-INF/views/gateconsole/Func/ViewInsert.jsp";
	}

	@Icon("update")
	@Name("Alterar")
	public String callUpdate()
	{
		if (isGET())
		{
			try
			{
				form = control.select(getForm().getId());
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
		return "/WEB-INF/views/gateconsole/Func/ViewUpdate.jsp";
	}

	@Icon("delete")
	@Name("Remover")
	public String callDelete()
	{
		try
		{
			control.delete(getForm());
			getMessages().add("O usuário foi removido com sucesso.");
		} catch (AppException e)
		{
			setMessages(e.getMessages());
		}
		return "/WEB-INF/views/gateconsole/Func/ViewResult.jsp";
	}

	public Func getForm()
	{
		if (form == null)
			form = new Func();
		return form;
	}

	public List<Func> getPage()
	{
		return page;
	}

	public static class UserScreen extends Screen
	{

		private Func func;
		private User user;
		private Page<User> page;

		@Inject
		private UserControl userControl;

		@Inject
		private UserControl.FuncControl control;

		@Name("Usuários")
		@Icon("gate.entity.User")
		public String call()
		{

			page = paginate(ordenate(control.search(func)));
			return "/WEB-INF/views/gateconsole/Func/User/View.jsp";
		}

		@Icon("insert")
		@Name("Adcionar")
		public String callInsert()
		{

			try
			{
				control.insert(user, func);
				user = null;
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
				control.delete(user, func);
				user = null;
			} catch (AppException ex)
			{
				setMessages(ex.getMessages());
			}
			return call();
		}

		@Override
		public User getUser()
		{
			if (user == null)
				user = new User();
			return user;
		}

		public Func getFunc()
		{
			if (func == null)
				func = new Func();
			return func;
		}

		public Page<User> getPage()
		{
			return page;
		}

		public List<User> getUsers()
		{
			return userControl.search();
		}
	}

	public static class RoleScreen extends Screen
	{

		private Func func;
		private Role role;
		private Page<Role> page;

		@Inject
		private RoleControl roleControl;

		@Inject
		private RoleControl.FuncControl control;

		@Name("Perfis")
		@Icon("gate.entity.Role")
		public String call()
		{

			page = paginate(ordenate(control.search(func)));
			return "/WEB-INF/views/gateconsole/Func/Role/View.jsp";
		}

		@Icon("insert")
		@Name("Adcionar")
		public String callInsert()
		{

			try
			{
				control.insert(role, func);
				role = null;
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
				control.delete(role, func);
				role = null;
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

		public Page<Role> getPage()
		{
			return page;
		}

		public List<Role> getRoles()
			throws AppException
		{
			return roleControl.search();
		}
	}
}
