package gate.adapter.converter;

import gate.type.Data;

public class DataConverterTest extends AbstractSimpleConverterTest<Data>
{
	@Override protected Class<Data> getType() {return Data.class;}
	@Override protected Data getValue() {return Data.valueOf("1K");}
	@Override protected String getString() {return "1K";}
}
