package gate.type;

public record RequestCommand(String module, String screen, String action)
	{

	public static final RequestCommand DEFAULT
		= new RequestCommand(null, null, null);

	public RequestCommand with(String module, String screen, String action)
	{
		if ("#".equals(module))
			module = this.module;
		if ("#".equals(screen))
			screen = this.screen;
		if ("#".equals(action))
			action = this.action;

		if (module != null && !module.isBlank())
			return new RequestCommand(module, screen, action);

		if (screen != null && !screen.isBlank())
			return new RequestCommand(this.module, screen, action);

		if (action != null && !action.isBlank())
			return new RequestCommand(this.module, this.screen, action);

		return new RequestCommand(this.module, this.screen, this.action);
	}
}
