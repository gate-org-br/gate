package gate.adapter.converter;

import gate.type.br.CEP;

public class CEPConverterTest extends AbstractSimpleConverterTest<CEP>
{
	@Override protected Class<CEP> getType() {return CEP.class;}
	@Override protected CEP getValue() {return CEP.valueOf("40100000");}
	@Override protected String getString() {return "40.100-000";}
}
