package gate.command;

import java.util.List;

public interface Command
{

	public static HideCommand hide()
	{
		return HideCommand.of();
	}

	public static HideCommand hide(String... messages)
	{
		return HideCommand.of(messages);
	}

	public static HideCommand hide(List<String> messages)
	{
		return HideCommand.of(messages);
	}

	public static ReloadCommand reload()
	{
		return ReloadCommand.of();
	}

	public static ReloadCommand reload(String... messages)
	{
		return ReloadCommand.of(messages);
	}

	public static ReloadCommand reload(List<String> messages)
	{
		return ReloadCommand.of(messages);
	}

	public static JSPCommand jsp(String name)
	{
		return JSPCommand.of(name);
	}

	public static HTMLCommand html(String name)
	{
		return HTMLCommand.of(name);
	}

	public static RedirectCommand redirect()
	{
		return RedirectCommand.of();
	}
}
