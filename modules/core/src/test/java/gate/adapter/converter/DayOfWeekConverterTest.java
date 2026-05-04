package gate.adapter.converter;

import java.time.DayOfWeek;

public class DayOfWeekConverterTest extends AbstractSimpleConverterTest<DayOfWeek>
{
	@Override protected Class<DayOfWeek> getType() {return DayOfWeek.class;}

	@Override protected DayOfWeek getValue() {return DayOfWeek.WEDNESDAY;}

	@Override protected String getString() {return "WEDNESDAY";}
}
