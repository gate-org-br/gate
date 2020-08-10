package gateconsole.screen;

import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.constraint.Required;
import gate.error.AppException;
import gateconsole.contol.SearchControl;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;

@Icon("2008")
@Name("Home")
public class HomeScreen extends Screen
{

	@Required
	@Description("O campo de busca deve conter pelo menos 3 caracteres.")
	private String form;
	private List<Object> page;

	@Override
	@Icon("search")
	@Name("Pesquisar")
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
