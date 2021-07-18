package gate.type;

import gate.annotation.Catcher;
import gate.annotation.Handler;
import gate.catcher.HTMLCatcher;
import gate.handler.HTMLHandler;
import java.nio.file.Path;
import java.util.Objects;

@Handler(HTMLHandler.class)
@Catcher(HTMLCatcher.class)
public class HTML
{

	private final String name;
	public static final HTML DEFAULT = new HTML();

	private HTML()
	{
		this.name = null;
	}

	public HTML(String name)
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

		path = path.resolve(action != null ? "View" + action + ".jsp" : "View.html");

		return path.toString();
	}
}
