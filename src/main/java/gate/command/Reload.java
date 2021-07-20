package gate.command;

import gate.annotation.Catcher;
import gate.annotation.Handler;
import gate.catcher.ReloadCatcher;
import gate.error.AppException;
import gate.handler.ReloadHandler;
import java.util.Collections;
import java.util.List;

@Handler(ReloadHandler.class)
@Catcher(ReloadCatcher.class)
public class Reload implements Command
{

	private final List<String> messages;

	private Reload(List<String> messages)
	{
		this.messages = Collections.unmodifiableList(messages);
	}

	public List<String> getMessages()
	{
		return messages;
	}

	public static final Reload of()
	{
		return new Reload(List.of());
	}

	public static final Reload of(String... messages)
	{
		return new Reload(List.of(messages));
	}

	public static final Reload of(List<String> messages)
	{
		return new Reload(Collections.unmodifiableList(messages));
	}

	public static final Reload of(Exception ex)
	{
		return new Reload(List.of(ex.getMessage()));
	}

	public static final Reload of(AppException ex)
	{
		return new Reload(Collections.unmodifiableList(ex.getMessages()));
	}
}
