package gate.http;

public record RequestCommand(String module, String screen, String action)
{
	static RequestCommand from(String module, String screen, String action, String path)
	{
		module = module != null && !module.isBlank() ? module : null;
		screen = screen != null && !screen.isBlank() ? screen : null;
		action = action != null && !action.isBlank() ? action : null;
		if (module == null && screen == null && action == null
		    && path != null && !path.isBlank()
		    && path.charAt(0) == '/')
		{
			int index = 0;

			StringBuilder m = new StringBuilder();
			for (index++; index < path.length() && path.charAt(index) != '/'; index++)
				m.append(path.charAt(index));
			if (index == path.length())
				return new RequestCommand(m.toString(), null, null);

			StringBuilder s = new StringBuilder();
			for (index++; index < path.length() && path.charAt(index) != '/'; index++)
				s.append(path.charAt(index));
			if (index == path.length())
				return new RequestCommand(m.toString(), s.toString(), null);

			StringBuilder a = new StringBuilder();
			for (index++; index < path.length() && path.charAt(index) != '/'; index++)
				a.append(path.charAt(index));
			return new RequestCommand(m.toString(), s.toString(), a.toString());
		}

		return new RequestCommand(module, screen, action);
	}

	public boolean isEmpty()
	{
		return module == null && screen == null && action == null;
	}
}