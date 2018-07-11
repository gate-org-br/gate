package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.IntervalsConverter;
import gate.annotation.ElementType;
import gate.annotation.Policonverter;
import gate.policonverter.IntervalsPoliconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ElementType(Interval.class)
@Converter(IntervalsConverter.class)
@Policonverter(IntervalsPoliconverter.class)
public class Intervals extends ArrayList<Interval>
{

	private static final long serialVersionUID = 1L;

	public Intervals()
	{
	}

	public Intervals(String value)
	{
		for (String string : value.split(",|;|\\r?\\n"))
			add(new Interval(string));
	}

	public Intervals(String... values)
	{
		for (String string : values)
			add(new Interval(string));
	}

	public Intervals(Interval... values)
	{
		this(Arrays.asList(values));
	}

	public Intervals(List<Interval> values)
	{
		values.stream().map(v -> v).forEach(v -> add(v));
	}

	@Override
	public String toString()
	{
		return stream().map(e -> e.toString()).collect(Collectors.joining(", "));
	}

	public String toString(String join)
	{
		return stream().map(e -> e.toString()).collect(Collectors.joining(join));
	}

	@Override
	public Intervals[] toArray()
	{
		return toArray(new Intervals[size()]);
	}
}
