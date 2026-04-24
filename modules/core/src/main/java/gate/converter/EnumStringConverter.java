package gate.converter;

import gate.error.ConversionException;

public class EnumStringConverter extends EnumConverter
{

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((Enum<?>) object).name() : "";
	}

}
