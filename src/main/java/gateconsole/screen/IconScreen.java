package gateconsole.screen;

import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Public;
import gate.base.Screen;

@Public
@Icon("2009")
@Name("Ícones")
@Description("Biblioteca de ícones do Gate")
public class IconScreen extends Screen
{

	public String call()
	{
		return "/WEB-INF/views/gateconsole/Icon/View.jsp";
	}
}
