package gate.adapter.converter.custom;

import gate.annotation.Description;
import gate.adapter.converter.Converter;
import gate.adapter.converter.ObjectConverter;
import gate.error.ConversionException;
import gate.type.Field;


@Description("Campos do tipo Field devem estar no formado JSON.")
public class FieldConverter extends ObjectConverter
{
	@Override
	public String render(Class<?> type, Object object)
	{
		if (object == null)
			return "";

		Field field = (Field) object;

		String value = Converter.render(field.getValue());

		if (field.getSize() != null)
		{
			int size = (int) Math.pow(2, field.getSize().ordinal());
			if (field.getMultiple())
				return String.format("<label data-size='%d'>%s: <span style='flex-basis: 60px; overflow: auto'><label>%s</label></span></label>", size, field.getName(), value);
			else
				return String.format("<label data-size='%d'>%s: <span><label>%s</label></span></label>", size, field.getName(), value);

		} else
		{
			if (field.getMultiple())
				return String.format("<label>%s: <span style='flex-basis: 60px; overflow: auto'><label>%s</label></span></label>", field.getName(), value);
			else
				return String.format("<label>%s: <span><label>%s</label></span></label>", field.getName(), value);
		}
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return render(type, object);
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;
		return Field.parse(string);
	}

}