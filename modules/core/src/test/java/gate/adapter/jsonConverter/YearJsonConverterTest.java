package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

import java.time.Year;

public class YearJsonConverterTest extends AbstractSimpleJsonConverterTest<Year>
{
	@Override protected Class<Year> getType() {return Year.class;}
	@Override protected Year getValue() {return Year.of(2026);}
	@Override protected JsonElement getJson() {return JsonString.wrap("2026");}
}