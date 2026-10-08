package gate.command;

import gate.type.RequestCommand;

import java.util.List;

public interface Command
{

	static HideCommand hide()
	{
		return HideCommand.of();
	}

	static HideCommand hide(String... messages)
	{
		return HideCommand.of(messages);
	}

	static HideCommand hide(List<String> messages)
	{
		return HideCommand.of(messages);
	}

	static ReloadCommand reload()
	{
		return ReloadCommand.of();
	}

	static ReloadCommand reload(String... messages)
	{
		return ReloadCommand.of(messages);
	}

	static ReloadCommand reload(List<String> messages)
	{
		return ReloadCommand.of(messages);
	}

	static HTMLCommand html(String name)
	{
		return HTMLCommand.of(name);
	}

	static RedirectCommand redirect()
	{
		return RedirectCommand.of();
	}

	static RedirectCommand redirect(RequestCommand command)
	{
		return RedirectCommand.of(command);
	}
}