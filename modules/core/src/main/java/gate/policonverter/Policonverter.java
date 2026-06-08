package gate.policonverter;

import gate.error.ConversionException;
import gate.util.Reflection;

import java.lang.reflect.Type;

public interface Policonverter
{

	Object toCollection(Type type, Object[] objects);

	Object getObject(Type type, String[] strings) throws ConversionException;

	String[] getString(Class<?> type, Object object);

	static Policonverter getPoliconverter(Type type)
	{
		return Policonverters.INSTANCE.get(Reflection.getRawType(type));
	}
}