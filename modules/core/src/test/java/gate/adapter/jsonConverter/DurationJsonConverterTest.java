package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonNumber;

import java.time.Duration;

public class DurationJsonConverterTest extends AbstractSimpleJsonConverterTest<Duration>
{
	@Override protected Class<Duration> getType() {return Duration.class;}
	@Override protected Duration getValue() {return Duration.ofHours(2);}
	@Override protected JsonElement getJson() {return JsonNumber.of(7200L);}
}
