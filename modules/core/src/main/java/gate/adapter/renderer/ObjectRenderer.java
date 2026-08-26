package gate.adapter.renderer;

public class ObjectRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object) {return object != null ? object.toString() : "";}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return String.format(format, render(type, object));
	}
}