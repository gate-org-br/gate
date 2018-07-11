package gateconsole.screen;

import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.error.AppException;


@Icon("2009")
@Name("Ícones")
@Description("Biblioteca de ícones do Gate")
public class IconScreen extends Screen
{

	public String call() throws AppException
	{
		return "/WEB-INF/views/gateconsole/Icon/View.jsp";
	}
}
