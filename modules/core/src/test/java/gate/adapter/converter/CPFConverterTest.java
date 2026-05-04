package gate.adapter.converter;

import gate.type.br.CPF;

public class CPFConverterTest extends AbstractSimpleConverterTest<CPF>
{
	@Override protected Class<CPF> getType() {return CPF.class;}

	@Override protected CPF getValue() {return CPF.valueOf(getString());}

	@Override protected String getString() {return "314.343.884-33";}
}