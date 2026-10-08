package gate.adapter.converter;

import gate.lang.json.JsonNumber;

public class JsonElementConverterTest extends AbstractSimpleConverterTest<JsonNumber>
{
	@Override protected Class<JsonNumber> getType() {return JsonNumber.class;}

	@Override protected JsonNumber getValue() {return JsonNumber.wrap(42L);}

	@Override protected String getString() {return "42";}
}