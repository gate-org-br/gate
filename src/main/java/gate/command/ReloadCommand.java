package gate.command;

import gate.annotation.Handler;
import gate.handler.ReloadCommandHandler;
import java.util.Collections;
import java.util.List;

@Handler(ReloadCommandHandler.class)
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

	static final ReloadCommand of()
	{
		return new ReloadCommand(List.of());
	}

	static final ReloadCommand of(String... messages)
	{
		return new ReloadCommand(List.of(messages));
	}

	static final ReloadCommand of(List<String> messages)
	{
		return new ReloadCommand(Collections.unmodifiableList(messages));
	}
}
