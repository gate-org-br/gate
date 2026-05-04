package gate.adapter.renderer;

import gate.type.Form;

import java.util.stream.Collectors;

public class FormRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		if (object instanceof Form form)
		{
			if (form.getFields().isEmpty())
				return "";
			return form.getFields().stream().map(Renderer::render)
					.collect(Collectors.joining("", "<fieldset>", "</fieldset>"));
		}
		return "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return render(type, object);
	}
}
