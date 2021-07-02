package gate.type;

import gate.annotation.Handler;
import gate.handler.JSPHandler;
import java.nio.file.Path;
import java.util.Objects;

@Handler(JSPHandler.class)
public class JSP
{

	private final String name;
	public static final JSP DEFAULT = new JSP();

	private JSP()
	{
		this.name = null;
	}

	public JSP(String name)
	{
		this.name = Objects.requireNonNull(name);
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
