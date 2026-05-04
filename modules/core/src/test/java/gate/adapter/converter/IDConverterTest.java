package gate.adapter.converter;

import gate.type.ID;

public class IDConverterTest extends AbstractSimpleConverterTest<ID>
{
	@Override protected Class<ID> getType() {return ID.class;}
	@Override protected ID getValue() {return ID.valueOf(1);}
	@Override protected String getString() {return "0000000001";}
}
