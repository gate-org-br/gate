package gate.command;

import gate.annotation.Catcher;
import gate.annotation.Handler;
import gate.catcher.HideCatcher;
import gate.error.AppException;
import gate.handler.HideHandler;
import java.util.Collections;
import java.util.List;

@Catcher(HideCatcher.class)
@Handler(HideHandler.class)
public class Hide implements Command
{

	private final List<String> messages;

	private Hide(List<String> messages)
	{
		this.messages = Collections.unmodifiableList(messages);
	}

	public List<String> getMessages()
	{
		return messages;
	}

	public static final Hide of()
	{
		return new Hide(List.of());
	}

	public static final Hide of(String... messages)
	{
		return new Hide(List.of(messages));
	}

	public static final Hide of(List<String> messages)
	{
		return new Hide(Collections.unmodifiableList(messages));
	}

	public static final Hide of(Exception ex)
	{
		return new Hide(List.of(ex.getMessage()));
	}

	public static final Hide of(AppException ex)
	{
		return new Hide(Collections.unmodifiableList(ex.getMessages()));
	}
}
