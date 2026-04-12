package gate.type;

import gate.util.Parameters;

public record RequestCommand(String module, String screen, String action)
{

	public RequestCommand
	{
		module = module != null && !module.isBlank() && !"*".equals(module) ? module.trim() : null;
		screen = screen != null && !screen.isBlank() && !"*".equals(screen) ? screen.trim() : null;
		action = action != null && !action.isBlank() && !"*".equals(action) ? action.trim() : null;
	}

	public static final RequestCommand DEFAULT
			= new RequestCommand(null, null, null);

	public RequestCommand with(RequestCommand command)
	{
		if (this.module != null)
			return new RequestCommand(
					"#".equals(this.module) ? command.module : this.module,
					"#".equals(this.screen) ? command.screen : this.screen,
					"#".equals(this.action) ? command.action : this.action);

		if (this.screen != null)
			return new RequestCommand(
					command.module,
					"#".equals(this.screen) ? command.screen : this.screen,
					"#".equals(this.action) ? command.action : this.action);

		if (this.action != null)
			return new RequestCommand(
					command.module,
					command.screen,
					"#".equals(this.action) ? command.action : this.action);

		return command;
	}

	public boolean matches(RequestCommand command)
	{
		if (command.action() != null
		    && !command.action().equals(action))
			return false;

		if (command.screen() != null
		    && !command.screen().equals(screen))
			return false;

		return command.module() == null
		       || command.module().equals(module);

	}

	@Override
	@SuppressWarnings("NullableProblems")
	public String toString()
	{
		Parameters parameters = new Parameters();
		parameters.put("MODULE", module());
		parameters.put("SCREEN", screen());
		parameters.put("ACTION", action());
		return parameters.isEmpty() ? "Gate" : "Gate?" + parameters;
	}

	public String toString(Parameters parameters)
	{
		parameters = new Parameters(parameters);
		parameters.put("MODULE", module());
		parameters.put("SCREEN", screen());
		parameters.put("ACTION", action());
		return parameters.isEmpty() ? "Gate" : "Gate?" + parameters;
	}

	public RequestCommand or(RequestCommand other)
	{
		return module != null || screen != null || action != null ? this : other;
	}

	public static RequestCommand ofPath(String path)
	{
		if (path == null || path.isBlank())
			return RequestCommand.DEFAULT;

		int index = 0;
		if (path.charAt(index) != '/')
			return RequestCommand.DEFAULT;

		StringBuilder module = new StringBuilder();
		for (index++; index < path.length() && path.charAt(index) != '/'; index++)
			module.append(path.charAt(index));

		if (index < path.length() && path.charAt(index) != '/')
			return new RequestCommand(module.toString(), null, null);

		StringBuilder screen = new StringBuilder();
		for (index++; index < path.length() && path.charAt(index) != '/'; index++)
			screen.append(path.charAt(index));

		if (index < path.length() && path.charAt(index) != '/')
			return new RequestCommand(module.toString(), screen.toString(), null);

		StringBuilder action = new StringBuilder();
		for (index++; index < path.length() && path.charAt(index) != '/'; index++)
			action.append(path.charAt(index));

		return new RequestCommand(module.toString(), screen.toString(), action.toString());
	}
}