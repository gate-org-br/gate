package gateconsole.screen;

import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.io.URL;

@Icon("2059")
@Name("Gerência")
@Description("Módulo de gerência")
public class Screen extends gate.base.Screen
{

	public URL call()
	{
		return new URL("Gate?MODULE=gateconsole.screen&SCREEN=Home");
	}
}
