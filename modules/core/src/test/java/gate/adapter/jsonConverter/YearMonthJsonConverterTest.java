package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

import java.time.YearMonth;

public class YearMonthJsonConverterTest extends AbstractSimpleJsonConverterTest<YearMonth>
{
	@Override protected Class<YearMonth> getType() {return YearMonth.class;}
	@Override protected YearMonth getValue() {return YearMonth.of(2026, 4);}
	@Override protected JsonElement getJson() {return JsonString.of("2026-04");}
}
