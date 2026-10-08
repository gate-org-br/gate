package gate.adapter.converter;

import gate.type.SIMCard;

public class SIMCardConverterTest extends AbstractSimpleConverterTest<SIMCard>
{
	@Override protected Class<SIMCard> getType() {return SIMCard.class;}
	@Override protected SIMCard getValue() {return SIMCard.valueOf("12345678901234567890");}
	@Override protected String getString() {return "12345678901234567890";}
}
