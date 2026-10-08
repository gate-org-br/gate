package gate.command;

import gate.error.AppException;
import gate.type.RequestCommand;
import gate.util.Parameters;

import java.util.Arrays;
import java.util.List;

public class RedirectCommand implements Command
{

	private final Parameters parameters = new Parameters();

	private RedirectCommand()
	{
	}

	static RedirectCommand of()
	{
		return new RedirectCommand();
	}

	static RedirectCommand of(RequestCommand command)
	{
		return new RedirectCommand()
				.module(command.module())
				.screen(command.screen())
				.action(command.action());
	}

	public RedirectCommand module(String module)
	{
		parameter("MODULE", module);
		return this;
	}

	public RedirectCommand screen(String screen)
	{
		parameter("SCREEN", screen);
		return this;
	}

	public RedirectCommand action(String action)
	{
		parameter("ACTION", action);
		return this;
	}

	public RedirectCommand parameter(String name, Object parameter)
	{
		if (parameter != null)
			parameters.put(name, parameter);
		else
			parameters.remove(name);
		return this;
	}

	public RedirectCommand messages(List<String> messages)
	{
		parameter("messages", !messages.isEmpty() ? messages : null);
		return this;
	}

	public RedirectCommand messages(String... messages)
	{
		messages(Arrays.asList(messages));
		return this;
	}

	public RedirectCommand exception(AppException exception)
	{
		messages(exception.getMessages());
		return this;
	}

	public RedirectCommand exception(Exception exception)
	{
		messages(exception.getMessage());
		return this;
	}

	public RedirectCommand tab(String tab)
	{
		parameter("tab", tab);
		return this;

	}

	@Override
	public String toString()
	{
		return "Gate?" + parameters;
	}

}