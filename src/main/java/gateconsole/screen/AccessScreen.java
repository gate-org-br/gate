package gateconsole.screen;

import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.Role;
import gate.entity.User;
import gate.error.AppException;
import gate.messaging.Messenger;
import gate.type.mime.MimeMail;
import gateconsole.contol.RoleControl;
import gateconsole.contol.UserControl;
import java.util.Collection;
import javax.inject.Inject;

@Icon("1007")
@Name("Pedidos de acesso")
public class AccessScreen extends Screen
{

	private User form;

	@Inject
	private Messenger messenger;

	@Inject
	private UserControl control;

	public String call()
	{
		return "/WEB-INF/views/gateconsole/Access/View.jsp";
	}

	public String callSelect()
	{
		try
		{
			form = control.select(getForm().getId());
			return "/WEB-INF/views/gateconsole/Access/ViewSelect.jsp";
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
			control.accept(getForm(), getForm().getRole());
			if (getForm().getEmail() != null)
				messenger.post(getUser().getEmail(), getForm().getEmail(), MimeMail.of("Cadastro acatado",
					"Seu pedido de cadastro foi acatado."));
			return "/WEB-INF/views/gateconsole/Access/ViewResult.jsp";
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
			form = control.select(getForm().getId());
			if (getForm().getEmail() != null)
				messenger.post(getUser().getEmail(), getForm().getEmail(), MimeMail.of("Cadastro recusado",
					"Seu pedido de cadastro foi recusado."));
			control.delete(getForm());
			return "/WEB-INF/views/gateconsole/Access/ViewResult.jsp";
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

	public Collection<Role> getRoles() throws AppException
	{
		return new RoleControl().search();
	}
}
