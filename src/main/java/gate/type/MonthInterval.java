package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.MonthIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

@Converter(MonthIntervalConverter.class)
public final class MonthInterval implements Serializable, Cloneable, Comparable<MonthInterval>
{

    private final Month month1;

    private final Month month2;

    public MonthInterval(final Month month1, final Month month2)
    {
	if (month1.getValue() > month2.getValue())
	    throw new IllegalArgumentException("month1 must be <= month2");
	if (month2.getValue() < month1.getValue())
	    throw new IllegalArgumentException("month2 must be >= month1");
	this.month1 = month1;
	this.month2 = month2;
    }

    public MonthInterval(int year)
    {
	if (year < 0)
	    throw new IllegalArgumentException("year must be >= 0");
	month1 = new Month(0, year);
	month2 = new Month(11, year);
    }

    public MonthInterval(String format, String month1, String month2) throws ParseException
    {
	this(new Month(format, month1), new Month(format, month2));
    }

    public MonthInterval(String month1, String month2) throws ParseException
    {
	this(new Month("MM/yyyy", month1), new Month("MM/yyyy", month2));
    }

    @Override
    protected MonthInterval clone() throws CloneNotSupportedException
    {
	return new MonthInterval(month1, month2);
    }

    public boolean contains(Month month)
    {
	return getMonth1().compareTo(month) <= 0 && getMonth2().compareTo(month) >= 0;
    }

    @Override
    public boolean equals(Object obj)
    {
	return (obj instanceof MonthInterval && ((MonthInterval) obj).month1.equals(month1) && ((MonthInterval) obj).month1.equals(month1));
    }

    public String format(String format)
    {
	return String.format("%s - %s", getMonth1().format(format), getMonth2().format(format));
    }

    public Month getMonth1()
    {
	return month1;
    }

    public Month getMonth2()
    {
	return month2;
    }

    public Collection<Month> getMonths()
    {
	Collection<Month> months = new ArrayList<Month>();
	for (Month month = month1; month.compareTo(month2) <= 0; month = month.getMonth().add(1))
	    months.add(month);
	return months;
    }

    @Override
    public int hashCode()
    {
	return (int) (month1.getValue() + month2.getValue());
    }

    @Override
    public String toString()
    {
	return format("MM/yyyy");
    }

    public long getValue()
    {
	return getMonth2().getValue() - getMonth1().getValue();
    }

    public Duration getDuration()
    {
	return Duration.of(getValue() / 1000);
    }

    @Override
    public int compareTo(MonthInterval monthInterval)
    {
	return (int) (getValue() - monthInterval.getValue());
    }
}
