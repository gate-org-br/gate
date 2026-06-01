package gate.adapter.renderer;

public class ArrayRenderer extends ObjectRenderer
{

	@Override
	public String render(Class<?> type, Object object)
	{
		StringBuilder string = new StringBuilder();
		for (Object obj : ((Object[]) object))
		{
			if (obj != null)
			{
				if (!string.isEmpty())
					string.append(", ");

				string.append(Renderer.render(obj));
			}
		}
		return string.toString();
	}
}