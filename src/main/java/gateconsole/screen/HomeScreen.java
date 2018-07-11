package gateconsole.screen;

import java.util.ArrayList;
import java.util.List;

import gate.error.AppException;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.constraint.Required;
import gateconsole.contol.SearchControl;

@Icon("2008")
@Name("Home")
public class HomeScreen extends gate.base.Screen
{

	@Required
	@Description("O campo de busca deve conter pelo menos 3 caracteres.")
	private String form;
	private List<Object> page;

	public Object call()
	{
		if (isPOST())
			try
			{
				setPage(new SearchControl().search(getForm()));
			} catch (AppException e)
			{
				setMessages(e.getMessages());
			}

		return "/WEB-INF/views/gateconsole/Home/View.jsp";
	}

	public String getForm()
	{
		return form;
	}

	public void setForm(String form)
	{
		this.form = form;
	}

	public List<Object> getPage()
	{
		if (page == null)
			page = new ArrayList<>();
		return page;
	}

	public void setPage(List<Object> page)
	{
		this.page = page;
	}
}
