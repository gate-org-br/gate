package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonNumber;

public class NumberJsonConverterTest extends AbstractSimpleJsonConverterTest<Integer>
{
	@Override protected Class<Integer> getType() {return Integer.class;}
	@Override protected Integer getValue() {return 42;}
	@Override protected JsonElement getJson() {return JsonNumber.wrap(42L);}
}