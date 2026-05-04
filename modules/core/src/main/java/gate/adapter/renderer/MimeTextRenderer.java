package gate.adapter.renderer;

import gate.type.mime.MimeText;

public class MimeTextRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? ((MimeText) object).getText() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, ((MimeText) object).getText()) : "";
	}
}
