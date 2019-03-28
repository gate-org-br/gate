package gateconsole.screen;

import gate.annotation.Asynchronous;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.constraint.Required;
import gate.entity.Func;
import gate.entity.User;
import gate.error.AppException;
import gate.error.ConversionException;
import gate.io.URL;
import gate.lang.property.Property;
import gate.report.Form;
import gate.report.Grid;
import gate.report.Report;
import gate.report.doc.Doc;
import gate.type.DataFile;
import gate.type.DateTime;
import gate.type.mime.MimeData;
import gate.util.Backup;
import gate.util.Page;
import gateconsole.contol.FuncControl;
import gateconsole.contol.UserControl;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;

@Name("Usuários")
@Icon("gate.entity.User")
public class UserScreen extends Screen
{

	private User form;
	private Doc.Type type;

	private Boolean value;

	@Required
	@Description("Entre com o arquivo a ser importado")
	private DataFile file;

	private Iterable<User> page;
	private final Backup BACKUP = new Backup("Usuários", User.class,
		"name", "userID", "email", "phone", "cellPhone", "details");

	@Inject
	private UserControl control;

	public String call()
	{
		if (isPOST() && getMessages().isEmpty())
			page = paginate(ordenate(control.search(getForm())));
		return "/WEB-INF/views/gateconsole/User/View.jsp";
	}

	@Name("Usuários")
	@Icon("gate.entity.User")
	public Object callImport() throws ServletException, IOException
	{
		page = control.search(getForm());
		return "/WEB-INF/views/gateconsole/User/ViewImport.jsp";
	}

	public String callNoRole()
	{
		page = paginate(ordenate(control.getSubscriptions()));
		return "/WEB-INF/views/gateconsole/User/ViewNoRole.jsp";
	}

	@Icon("select")
	@Name("Detalhes")
	public String callSelect()
	{
		try
		{
			form = control.select(getForm().getId());
			return "/WEB-INF/views/gateconsole/User/ViewSelect.jsp";
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
		if (isPOST()
			&& getMessages().isEmpty())
		{
			try
			{
				control.insert(getForm());
				return callSelect();
			} catch (AppException e)
			{
				setMessages(e.getMessages());
			}
		} else
			getForm().setActive(true);
		return "/WEB-INF/views/gateconsole/User/ViewInsert.jsp";
	}

	@Icon("update")
	@Name("Alterar")
	public String callUpdate() throws AppException
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
		return "/WEB-INF/views/gateconsole/User/ViewUpdate.jsp";
	}

	@Icon("passwd")
	@Name("Resetar senha")
	public String callPasswd() throws AppException
	{
		control.passwd(getForm());
		getMessages().add("Senha do usuário resetada para o login.");
		return callSelect();
	}

	@Icon("delete")
	@Name("Remover")
	public String callDelete() throws AppException
	{
		try
		{
			control.delete(getForm());
			getMessages().add("O usuário foi removido com sucesso.");
		} catch (AppException e)
		{
			setMessages(e.getMessages());
		}
		return "/WEB-INF/views/gateconsole/User/ViewResult.jsp";
	}

	@Icon("upload")
	@Name("Importar")
	public String callUpload()
	{
		return "/WEB-INF/views/gateconsole/User/ViewUpload.jsp";
	}

	@Asynchronous
	public URL callCommit() throws ConversionException, AppException
	{
		control.insert(getForm().getRole(), BACKUP.load(file));
		return new URL("Gate").setModule(getModule()).setScreen(getScreen());
	}

	@Icon("report")
	@Name("Relatório")
	public Doc callReport()
	{
		Report report = new Report();

		report.addImage(getOrg().getIcon());
		report.addHeader(new DateTime());
		report.addHeader("Relatórios de Usuários");
		report.addHeader(getApp().getId() + " - " + getApp().getName());

		Form form = report.addForm(4);

		form.setCaption("Filtro");
		form.add("Ativo:", getForm().getActive());
		form.add("Login:", getForm().getUserID());
		form.add("E-Mail:", getForm().getEmail()).colspan(2);
		form.add("Nome", getForm().getName()).colspan(2);
		form.add("Perfil", getForm().getRole().getName()).colspan(2);

		report.addLineBreak();

		Grid<User> grid = report.addGrid(User.class, ordenate(control.search(getForm())));
		grid.add().head("Login").body(User::getUserID).style().width(10);
		grid.add().head("Perfil").body(e -> e.getRole().getName()).style().width(45);
		grid.add().head("Name").body(User::getName).style().width(45);

		return Doc.create(getType(), report);
	}

	public User getForm()
	{
		if (form == null)
			form = new User();
		return form;
	}

	public Iterable<User> getPage()
	{
		return page;
	}

	public DataFile getFile()
	{
		return file;
	}

	public void setFile(DataFile file)
	{
		this.file = file;
	}

	public List<Property> getProperties()
	{
		return BACKUP.getProperties();
	}

	public Doc.Type getType()
	{
		return type;
	}

	public void setType(Doc.Type type)
	{
		this.type = type;
	}

	public void setForm(User form)
	{
		this.form = form;
	}

	public MimeData getPhoto()
	{
		return control.getPhoto(getForm().getId());
	}

	public static class FuncScreen extends Screen
	{

		private Func func;
		private User user;
		private Page<Func> page;

		@Inject
		private FuncControl funcControl;

		@Inject
		private FuncControl.UserControl control;

		@Name("Funções")
		@Icon("gate.entity.Func")
		public String call()
		{

			page = paginate(ordenate(control.search(user)));
			return "/WEB-INF/views/gateconsole/User/Func/View.jsp";
		}

		@Icon("insert")
		@Name("Adcionar")
		public String callInsert()
		{

			try
			{
				control.insert(func, user);
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
				control.delete(func, user);
				func = null;
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

		public Page<Func> getPage()
		{
			return page;
		}

		public List<Func> getFuncs()
		{
			return funcControl.search();
		}
	}

	public Boolean getValue()
	{
		return value;
	}

	public void setValue(Boolean value)
	{
		this.value = value;
	}

}
