package gate.adapter.converter;

import gate.type.br.CTPS;

public class CTPSConverterTest extends AbstractSimpleConverterTest<CTPS>
{
	@Override protected Class<CTPS> getType() {return CTPS.class;}
	@Override protected CTPS getValue() {return CTPS.valueOf("12345 67890-BA");}
	@Override protected String getString() {return "12345 67890-BA";}
}
