package gateconsole.screen;

import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Public;
import gate.base.Screen;

@Public
@Icon("2072")
@Name("Demo")
@Description("Demonstração")
public class DemoScreen extends Screen
{

	public String call()
	{
		return "/WEB-INF/views/gateconsole/Demo/View.jsp";
	}
}
