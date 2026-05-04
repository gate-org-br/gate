package gate.adapter.converter;

import gate.type.IMEI;

public class IMEIConverterTest extends AbstractSimpleConverterTest<IMEI>
{
	@Override protected Class<IMEI> getType() {return IMEI.class;}
	@Override protected IMEI getValue() {return IMEI.valueOf("35-209900-176148-1");}
	@Override protected String getString() {return "35-209900-176148-1";}
}
