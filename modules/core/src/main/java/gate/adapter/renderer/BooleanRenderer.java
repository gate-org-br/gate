package gate.adapter.renderer;

public class BooleanRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? Boolean.TRUE.equals(object) ? "Sim" : "Não" : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, Boolean.TRUE.equals(object) ? "Sim" : "Não") : "";
	}
}
