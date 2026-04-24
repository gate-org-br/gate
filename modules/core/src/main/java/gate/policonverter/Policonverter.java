package gate.policonverter;

import gate.error.ConversionException;

public interface Policonverter
{

	Object toCollection(Class<?> type, Object[] objects);

	Object getObject(Class<?> type, String[] strings) throws ConversionException;

	String[] getString(Class<?> type, Object object);

	static Policonverter getPoliconverter(Class<?> type)
	{
		return Policonverters.INSTANCE.get(type);
	}
}
