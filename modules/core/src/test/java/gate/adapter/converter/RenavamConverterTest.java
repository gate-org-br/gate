package gate.adapter.converter;

import gate.type.br.Renavam;

public class RenavamConverterTest extends AbstractSimpleConverterTest<Renavam>
{
	@Override protected Class<Renavam> getType() {return Renavam.class;}
	@Override protected Renavam getValue() {return Renavam.valueOf("0063988496-2");}
	@Override protected String getString() {return "0063988496-2";}
}
