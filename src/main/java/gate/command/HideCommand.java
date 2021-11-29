package gate.command;

import gate.annotation.Handler;
import gate.handler.HideCommandHandler;
import java.util.Collections;
import java.util.List;

@Handler(HideCommandHandler.class)
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

	static final HideCommand of()
	{
		return new HideCommand(List.of());
	}

	static final HideCommand of(String... messages)
	{
		return new HideCommand(List.of(messages));
	}

	static final HideCommand of(List<String> messages)
	{
		return new HideCommand(Collections.unmodifiableList(messages));
	}
}
