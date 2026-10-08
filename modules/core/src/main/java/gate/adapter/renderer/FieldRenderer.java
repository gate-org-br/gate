package gate.adapter.renderer;

import gate.type.Field;

public class FieldRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		if (object == null)
			return "";

		Field field = (Field) object;
		String value = Renderer.render(field.getValue());
		Field.Schema schema = field.schema();

		if (schema.size() != null)
		{
			int size = (int) Math.pow(2, schema.size().ordinal());
			if (schema.multiple())
				return String.format("<label data-size='%d'>%s: <span style='flex-basis: 60px; overflow: auto'><label>%s</label></span></label>", size, schema.name(), value);
			else
				return String.format("<label data-size='%d'>%s: <span><label>%s</label></span></label>", size, schema.name(), value);
		} else
		{
			if (schema.multiple())
				return String.format("<label>%s: <span style='flex-basis: 60px; overflow: auto'><label>%s</label></span></label>", schema.name(), value);
			else
				return String.format("<label>%s: <span><label>%s</label></span></label>", schema.name(), value);
		}
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return render(type, object);
	}
}
