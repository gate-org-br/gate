package gate.adapter.converter;

import java.time.Year;

public class YearConverterTest extends AbstractSimpleConverterTest<Year>
{
	@Override protected Class<Year> getType() {return Year.class;}

	@Override protected Year getValue() {return Year.parse(getString());}

	@Override protected String getString() {return "2026";}
}
