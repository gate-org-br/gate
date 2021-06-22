package gate.type;

import gate.annotation.Handler;
import gate.error.AppException;
import gate.handler.HideCommandHandler;
import java.util.Collections;
import java.util.List;

@Handler(HideCommandHandler.class)
public class HideCommand
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

	public static final HideCommand of()
	{
		return new HideCommand(List.of());
	}

	public static final HideCommand of(String... messages)
	{
		return new HideCommand(List.of(messages));
	}

	public static final HideCommand of(List<String> messages)
	{
		return new HideCommand(Collections.unmodifiableList(messages));
	}

	public static final HideCommand of(Exception ex)
	{
		return new HideCommand(List.of(ex.getMessage()));
	}

	public static final HideCommand of(AppException ex)
	{
		return new HideCommand(Collections.unmodifiableList(ex.getMessages()));
	}
}
