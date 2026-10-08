package gate.command;

import java.util.Collections;
import java.util.List;

public class ReloadCommand implements Command
{

	private final List<String> messages;

	private ReloadCommand(List<String> messages)
	{
		this.messages = Collections.unmodifiableList(messages);
	}

	public List<String> getMessages()
	{
		return messages;
	}

	static ReloadCommand of()
	{
		return new ReloadCommand(List.of());
	}

	static ReloadCommand of(String... messages)
	{
		return new ReloadCommand(List.of(messages));
	}

	static ReloadCommand of(List<String> messages)
	{
		return new ReloadCommand(Collections.unmodifiableList(messages));
	}
}