package gate.converter.custom;

import gate.annotation.Description;
import gate.converter.CollectionConverter;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import gate.type.Form;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.stream.Collectors;

@Description("Campos do tipo Form devem estar no formado JSON.")
public class FormConverter extends CollectionConverter
{
	@Override
	public String render(Class<?> type, Object object)
	{
		if (object instanceof Form form)
		{
			if (form.getFields().isEmpty())
				return "";
			return form.getFields().stream().map(Converter::render)
					.collect(Collectors.joining("", "<fieldset>", "</fieldset>"));
		}
		return "";
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
		try
		{
			return string != null && !string.trim().isEmpty()
					? Form.valueOf(string) : null;
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(string.concat(" não é um Formulário válido."));
		}
	}

	@Override
	public <T> void toJson(Deque<Object> stack, JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		writer.write(object.toString());
	}

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
	{
		if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_ARRAY)
			throw new ConversionException(scanner.getCurrent() + " is not a form");

		Form form = new Form();
		Converter converter = Converter.getConverter(gate.type.Field.class);

		do
		{
			scanner.scan();
			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
				form.getFields().add((gate.type.Field) converter.ofJson(scanner, gate.type.Field.class, null));
			else if (!form.getFields().isEmpty())
				throw new ConversionException(scanner.getCurrent() + " is not a form");
		} while (JsonToken.Type.COMMA == scanner.getCurrent().getType());

		if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
			throw new ConversionException(scanner.getCurrent() + " is not a form");

		scanner.scan();
		return form;

	}

	@Override
	public <T> void toJsonText(Deque<Object> stack, JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		writer.write(object.toString());
	}

}