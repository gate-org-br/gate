package gate.adapter.converter;

import gate.type.br.CNPJ;

public class CNPJConverterTest extends AbstractSimpleConverterTest<CNPJ>
{
	@Override protected Class<CNPJ> getType() {return CNPJ.class;}
	@Override protected CNPJ getValue() {return CNPJ.valueOf("69.362.335/0001-56");}
	@Override protected String getString() {return "69.362.335/0001-56";}
}
