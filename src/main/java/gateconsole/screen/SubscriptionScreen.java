package gateconsole.screen;

import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.User;

import gate.entity.Role;
import gate.error.AppException;
import gate.type.mime.MimeMail;
import gate.messaging.Messenger;
import gateconsole.contol.RoleControl;
import gateconsole.contol.UserControl;
import java.util.Collection;

import java.util.List;
import javax.inject.Inject;

@Icon("2002")
@Name("Requisições de Cadastro")
public class SubscriptionScreen extends Screen
{

	private User form;
	private List<User> page;
	@Inject
	Messenger messenger;

	public String call()
	{
		setPage(new UserControl().getSubscriptions());
		return "/WEB-INF/views/gateconsole/Subscription/View.jsp";
	}

	public String callSelect()
	{
		try
		{
			setForm(new UserControl().select(getForm().getId()));
			return "/WEB-INF/views/gateconsole/Subscription/ViewSelect.jsp";
		} catch (AppException e)
		{
			setMessages(e.getMessages());
			return call();
		}
	}

	public String callUpdate()
	{
		try
		{
			new UserControl().accept(getForm(), getForm().getRole());
			if (getForm().getEmail() != null)
				messenger.post(getUser().getEmail(), getForm().getEmail(), MimeMail.of("Cadastro acatado",
					"Seu pedido de cadastro foi acatado."));
			return "/WEB-INF/views/gateconsole/Subscription/ViewResult.jsp";
		} catch (AppException e)
		{
			setMessages(e.getMessages());
			return callSelect();
		}
	}

	public String callDelete()
	{
		try
		{
			setForm(new UserControl().select(getForm().getId()));
			if (getForm().getEmail() != null)
				messenger.post(getUser().getEmail(), getForm().getEmail(), MimeMail.of("Cadastro recusado",
					"Seu pedido de cadastro foi recusado."));
			new UserControl().delete(getForm());
			return "/WEB-INF/views/gateconsole/Subscription/ViewResult.jsp";
		} catch (AppException e)
		{
			setMessages(e.getMessages());
			return callSelect();
		}
	}

	public User getForm()
	{
		if (form == null)
			form = new User();
		return form;
	}

	public void setForm(User form)
	{
		this.form = form;
	}

	public List<User> getPage()
	{
		return page;
	}

	public void setPage(List<User> page)
	{
		this.page = page;
	}

	public Collection<Role> getRoles()
	{
		return new RoleControl().search();
	}
}
