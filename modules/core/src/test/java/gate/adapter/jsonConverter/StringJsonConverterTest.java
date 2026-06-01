package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

public class StringJsonConverterTest extends AbstractSimpleJsonConverterTest<String>
{
	@Override protected Class<String> getType() {return String.class;}
	@Override protected String getValue() {return "hello";}
	@Override protected JsonElement getJson() {return JsonString.wrap("hello");}
}