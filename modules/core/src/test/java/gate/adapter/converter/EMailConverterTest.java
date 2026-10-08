package gate.adapter.converter;

import gate.type.EMail;

public class EMailConverterTest extends AbstractSimpleConverterTest<EMail>
{
	@Override protected Class<EMail> getType() {return EMail.class;}
	@Override protected EMail getValue() {return EMail.valueOf("user@example.com");}
	@Override protected String getString() {return "user@example.com";}
}
