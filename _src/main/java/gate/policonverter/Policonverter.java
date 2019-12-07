package gate.policonverter;

import gate.error.ConversionException;
import javax.servlet.http.Part;

public interface Policonverter
{

	public abstract Object toCollection(Class<?> type, Object[] objects);

	public abstract Object getObject(Class<?> type, String[] strings) throws ConversionException;

	public abstract String[] getString(Class<?> type, Object object);

	public abstract Object getObject(Class<?> type, Part[] parts) throws ConversionException;

	static Policonverter getPoliconverter(Class<?> type)
	{
		return Policonverters.INSTANCE.get(type);
	}
}
