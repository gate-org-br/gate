package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

import java.time.DayOfWeek;

public class EnumJsonConverterTest extends AbstractSimpleJsonConverterTest<DayOfWeek>
{
	@Override protected Class<DayOfWeek> getType() {return DayOfWeek.class;}
	@Override protected DayOfWeek getValue() {return DayOfWeek.MONDAY;}
	@Override protected JsonElement getJson() {return JsonString.of("MONDAY");}
}
