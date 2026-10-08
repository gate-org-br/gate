package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

import java.time.LocalDateTime;

public class LocalDateTimeJsonConverterTest extends AbstractSimpleJsonConverterTest<LocalDateTime>
{
	@Override
	protected Class<LocalDateTime> getType() {return LocalDateTime.class;}
	@Override
	protected LocalDateTime getValue() {return LocalDateTime.of(2026, 4, 29, 14, 35);}
	@Override
	protected JsonElement getJson() {return JsonString.wrap("29/04/2026 14:35");}
}