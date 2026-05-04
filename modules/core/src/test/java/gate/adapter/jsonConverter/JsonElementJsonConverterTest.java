package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonNumber;

public class JsonElementJsonConverterTest extends AbstractSimpleJsonConverterTest<JsonNumber>
{
	@Override protected Class<JsonNumber> getType() {return JsonNumber.class;}
	@Override protected JsonNumber getValue() {return JsonNumber.of(42L);}
	@Override protected JsonElement getJson() {return JsonNumber.of(42L);}
}
