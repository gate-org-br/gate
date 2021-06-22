package gate.type;

import gate.annotation.Handler;
import gate.handler.HTMLTemplateHandler;

@Handler(HTMLTemplateHandler.class)
public class HTMLTemplate
{

	private final String name;

	public HTMLTemplate(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
