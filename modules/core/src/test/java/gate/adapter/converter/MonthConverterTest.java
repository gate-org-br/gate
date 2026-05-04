package gate.adapter.converter;

import java.time.Month;

public class MonthConverterTest extends AbstractSimpleConverterTest<Month>
{
	@Override protected Class<Month> getType() {return Month.class;}

	@Override protected Month getValue() {return Month.APRIL;}

	@Override protected String getString() {return "APRIL";}
}
