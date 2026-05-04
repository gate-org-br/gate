package gate.adapter.jsonConverter;

import gate.lang.json.JsonBoolean;
import gate.lang.json.JsonElement;

public class BooleanJsonConverterTest extends AbstractSimpleJsonConverterTest<Boolean>
{
	@Override protected Class<Boolean> getType() {return Boolean.class;}
	@Override protected Boolean getValue() {return Boolean.TRUE;}
	@Override protected JsonElement getJson() {return JsonBoolean.TRUE;}
}
