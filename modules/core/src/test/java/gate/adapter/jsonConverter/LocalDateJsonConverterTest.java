package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

import java.time.LocalDate;

public class LocalDateJsonConverterTest extends AbstractSimpleJsonConverterTest<LocalDate>
{
	@Override protected Class<LocalDate> getType() {return LocalDate.class;}
	@Override protected LocalDate getValue() {return LocalDate.of(2026, 4, 29);}
	@Override protected JsonElement getJson() {return JsonString.wrap("2026-04-29");}
}