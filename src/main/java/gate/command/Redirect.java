package gate.command;

import gate.annotation.Catcher;
import gate.annotation.Handler;
import gate.catcher.RedirectCatcher;
import gate.error.AppException;
import gate.handler.RedirectHandler;
import gate.util.Parameters;
import java.util.Arrays;
import java.util.List;

@Handler(RedirectHandler.class)
@Catcher(RedirectCatcher.class)
public class Redirect implements Command
{

	private final Parameters parameters = new Parameters();

	private Redirect()
	{
	}

	public static Redirect module(String string)
	{
		return new Redirect().parameter("MODULE", string);
	}

	public Redirect screen(String screen)
	{
		parameter("SCREEN", screen);
		return this;
	}

	public Redirect action(String action)
	{
		parameter("ACTION", action);
		return this;
	}

	public Redirect parameter(String name, Object parameter)
	{
		if (parameter != null)
			parameters.put(name, parameter);
		else
			parameters.remove(name);
		return this;
	}

	public Redirect messages(List<String> messages)
	{
		parameter("messages", !messages.isEmpty() ? messages : null);
		return this;
	}

	public Redirect messages(String... messages)
	{
		messages(Arrays.asList(messages));
		return this;
	}

	public Redirect exception(AppException exception)
	{
		messages(exception.getMessages());
		return this;
	}

	public Redirect exception(Exception exception)
	{
		messages(exception.getMessage());
		return this;
	}

	public Redirect tab(String tab)
	{
		parameter("tab", tab);
		return this;

	}

	@Override
	public String toString()
	{
		return "Gate?" + parameters.toString();
	}

}
