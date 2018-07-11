package gate.policonverter;

import gate.error.ConversionException;
import gate.type.Intervals;

import java.util.stream.Stream;
import javax.servlet.http.Part;

public class IntervalsPoliconverter extends Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value) throws ConversionException
	{
		return new Intervals(value);
	}

	@Override
	public Object getObject(Class<?> type, Part[] value) throws ConversionException
	{
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public String[] getString(Class<?> type, Object value) throws ConversionException
	{
		return Stream.of(((Intervals) value)).map(e -> e.toString()).toArray(String[]::new);
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		throw new java.lang.UnsupportedOperationException();
	}
}
