package gate.type;

import gate.annotation.Handler;
import gate.error.AppException;
import gate.handler.ReloadCommandHandler;
import java.util.Collections;
import java.util.List;

@Handler(ReloadCommandHandler.class)
public class ReloadCommand
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

	public static final ReloadCommand of()
	{
		return new ReloadCommand(List.of());
	}

	public static final ReloadCommand of(String... messages)
	{
		return new ReloadCommand(List.of(messages));
	}

	public static final ReloadCommand of(List<String> messages)
	{
		return new ReloadCommand(Collections.unmodifiableList(messages));
	}

	public static final ReloadCommand of(Exception ex)
	{
		return new ReloadCommand(List.of(ex.getMessage()));
	}

	public static final ReloadCommand of(AppException ex)
	{
		return new ReloadCommand(Collections.unmodifiableList(ex.getMessages()));
	}
}
