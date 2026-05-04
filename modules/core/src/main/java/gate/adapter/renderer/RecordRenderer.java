package gate.adapter.renderer;

public class RecordRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString().trim() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, render(type, object)) : "";
	}
}
