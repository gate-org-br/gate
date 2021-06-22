package gate.type;

import gate.annotation.Handler;
import gate.handler.JSPTemplateHandler;

@Handler(JSPTemplateHandler.class)
public class JSPTemplate
{

	private final String name;

	public JSPTemplate(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
