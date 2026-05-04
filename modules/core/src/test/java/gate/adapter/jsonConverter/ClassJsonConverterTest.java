package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

public class ClassJsonConverterTest extends AbstractSimpleJsonConverterTest<Class>
{
	@Override protected Class<Class> getType() {return Class.class;}
	@Override protected Class getValue() {return String.class;}
	@Override protected JsonElement getJson() {return JsonString.of("java.lang.String");}
}
