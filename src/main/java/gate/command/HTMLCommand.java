package gate.command;

import gate.annotation.Handler;
import gate.handler.HTMLCommandHandler;
import java.nio.file.Path;
import java.util.Objects;

@Handler(HTMLCommandHandler.class)
public class HTMLCommand implements Command
{

	private final String name;
	public static final HTMLCommand DEFAULT = new HTMLCommand();

	private HTMLCommand()
	{
		this.name = null;
	}

	private HTMLCommand(String name)
	{
		this.name = name;
	}

	static HTMLCommand of(String name)
	{
		return new HTMLCommand(Objects.requireNonNull(name));
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static String getPath(String module, String screen, String action)
	{
		Path path = Path.of("WEB-INF", "views");

		int i = 0;
		StringBuilder directory = new StringBuilder();
		while (i < module.length())
		{
			if ('.' == module.charAt(i))
			{
				path = path.resolve(directory.toString());
				directory.setLength(0);
				i++;
			} else
				directory.append(module.charAt(i++));
		}
		path = path.resolve(directory.toString());

		if (screen != null)
			path = path.resolve(screen);

		path = path.resolve(action != null ? "View" + action + ".jsp" : "View.html");

		return path.toString();
	}
}
