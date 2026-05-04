package gate.adapter.converter;

import gate.annotation.Description;
import gate.error.ConversionException;
import gate.type.Form;

import java.lang.reflect.Type;

@Description("Campos do tipo Form devem estar no formado JSON.")
public class FormConverter implements Converter
{
	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
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
}