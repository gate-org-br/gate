package gateconsole.screen;

import gate.annotation.Current;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.App;
import gate.error.AppException;

import java.util.Collection;

import javax.inject.Inject;

@Icon("2044")
@Name("Aplicações")
public class AppScreen extends Screen
{

	private App form;
	private String id;

	@Inject
	@Current
	private Collection<App> page;

	public String call() throws AppException
	{
		return "/WEB-INF/views/gateconsole/App/View.jsp";
	}

	@Icon("2199")
	@Name("Detalhes")
	public String callSelect() throws AppException
	{
		form = page.stream().filter(e -> e.getId().equals(id)).findAny().orElse(null);
		return "/WEB-INF/views/gateconsole/App/ViewSelect.jsp";
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public App getForm()
	{
		return form;
	}

	public Collection<App> getPage()
	{
		return page;
	}
}
