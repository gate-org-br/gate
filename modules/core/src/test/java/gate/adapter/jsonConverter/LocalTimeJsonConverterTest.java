package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

import java.time.LocalTime;

public class LocalTimeJsonConverterTest extends AbstractSimpleJsonConverterTest<LocalTime>
{
	@Override protected Class<LocalTime> getType() {return LocalTime.class;}
	@Override protected LocalTime getValue() {return LocalTime.of(14, 35);}
	@Override protected JsonElement getJson() {return JsonString.wrap("14:35");}
}