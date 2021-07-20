package gate.command;

import gate.annotation.Catcher;
import gate.annotation.Handler;
import gate.catcher.JSPCatcher;
import gate.handler.JSPHandler;
import java.nio.file.Path;
import java.util.Objects;

@Handler(JSPHandler.class)
@Catcher(JSPCatcher.class)
public class JSP implements Command
{

	private final String name;
	public static final JSP DEFAULT = new JSP();

	private JSP()
	{
		this.name = null;
	}

	private JSP(String name)
	{
		this.name = name;
	}

	public static JSP of(String name)
	{
		return new JSP(Objects.requireNonNull(name));
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
