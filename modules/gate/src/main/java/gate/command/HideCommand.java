package gate.command;

import java.util.Collections;
import java.util.List;

public class HideCommand implements Command
{

	private final List<String> messages;

	private HideCommand(List<String> messages)
	{
		this.messages = Collections.unmodifiableList(messages);
	}

	public List<String> getMessages()
	{
		return messages;
	}

	static HideCommand of()
	{
		return new HideCommand(List.of());
	}

	static HideCommand of(String... messages)
	{
		return new HideCommand(List.of(messages));
	}

	static HideCommand of(List<String> messages)
	{
		return new HideCommand(Collections.unmodifiableList(messages));
	}
}