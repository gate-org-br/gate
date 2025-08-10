package gate.type;

public record RequestCommand(String module, String screen, String action)
	{

	public boolean isEmpty()
	{
		return (module == null || module.isBlank())
			&& (screen == null || screen.isBlank())
			&& (action == null || action.isBlank());
	}
}
