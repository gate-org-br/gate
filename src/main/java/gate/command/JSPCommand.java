package gate.command;

import gate.annotation.Handler;
import gate.handler.JSPCommandHandler;
import java.nio.file.Path;
import java.util.Objects;

@Handler(JSPCommandHandler.class)
public class JSPCommand implements Command
{

	private final String name;
	public static final JSPCommand DEFAULT = new JSPCommand();

	private JSPCommand()
	{
		this.name = null;
	}

	private JSPCommand(String name)
	{
		this.name = name;
	}

	static JSPCommand of(String name)
	{
		return new JSPCommand(Objects.requireNonNull(name));
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

		path = path.resolve(action != null ? "View" + action + ".jsp" : "View.jsp");

		return path.toString();
	}
}
