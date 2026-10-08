package gate.adapter.converter;

import gate.type.Range;

public class RangeConverterTest extends AbstractSimpleConverterTest<Range>
{
	@Override protected Class<Range> getType() {return Range.class;}
	@Override protected Range getValue() {return Range.of(1, 2);}
	@Override protected String getString() {return "1 - 2";}
}
