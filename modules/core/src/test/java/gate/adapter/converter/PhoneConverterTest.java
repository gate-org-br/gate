package gate.adapter.converter;

import gate.type.br.Phone;

public class PhoneConverterTest extends AbstractSimpleConverterTest<Phone>
{
	@Override protected Class<Phone> getType() {return Phone.class;}
	@Override protected Phone getValue() {return Phone.valueOf("71988958168");}
	@Override protected String getString() {return "71988958168";}
}
