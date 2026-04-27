package gate.sql.registrar;

import gate.lang.json.*;
import gate.sql.columnMapper.*;
import gate.type.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.Map;

public class SqlColumnMapperRegistrar implements ColumnMapperRegistrar
{
	@Override
	public void register(Map<Class<?>, ColumnMapper> registry)
	{
		registry.put(BigDecimal.class, new BigDecimalColumnMapper());
		registry.put(boolean.class, new BooleanColumnMapper());
		registry.put(Boolean.class, new BooleanColumnMapper());
		registry.put(byte.class, new ByteColumnMapper());
		registry.put(Byte.class, new ByteColumnMapper());
		registry.put(char.class, new CharacterColumnMapper());
		registry.put(Character.class, new CharacterColumnMapper());
		registry.put(Class.class, new ClassColumnMapper());
		registry.put(double.class, new DoubleColumnMapper());
		registry.put(Double.class, new DoubleColumnMapper());
		registry.put(Duration.class, new DurationColumnMapper());
		registry.put(Enum.class, new EnumColumnMapper());
		registry.put(float.class, new FloatColumnMapper());
		registry.put(Float.class, new FloatColumnMapper());
		registry.put(int.class, new IntegerColumnMapper());
		registry.put(Integer.class, new IntegerColumnMapper());
		registry.put(LocalDate.class, new LocalDateColumnMapper());
		registry.put(LocalDateTime.class, new LocalDateTimeColumnMapper());
		registry.put(LocalTime.class, new LocalTimeColumnMapper());
		registry.put(long.class, new LongColumnMapper());
		registry.put(Long.class, new LongColumnMapper());
		registry.put(Money.class, new MoneyColumnMapper());
		registry.put(Number.class, new BigDecimalColumnMapper());
		registry.put(Percentage.class, new PercentageColumnMapper());
		registry.put(short.class, new ShortColumnMapper());
		registry.put(Short.class, new ShortColumnMapper());
		registry.put(String.class, new StringColumnMapper());
		registry.put(Tax.class, new TaxColumnMapper());
		registry.put(YearMonth.class, new YearMonthColumnMapper());
		registry.put(byte[].class, new ByteArrayColumnMapper());
		registry.put(Year.class, new YearColumnMapper());

		registry.put(YearMonthInterval.class, new YearMonthIntervalColumnMapper());
		registry.put(LocalDateInterval.class, new LocalDateIntervalColumnMapper());
		registry.put(LocalDateTimeInterval.class, new LocalDateTimeIntervalColumnMapper());
		registry.put(LocalTimeInterval.class, new LocalTimeIntervalColumnMapper());
		registry.put(TempFile.class, new TempFileColumnMapper());
		registry.put(NamedTempFile.class, new NamedTempFileColumnMapper());
		registry.put(Range.class, new RangeColumnMapper());
		registry.put(DataFile.class, new DataFileColumnMapper());

		registry.put(JsonElement.class, new JsonElementColumnMapper());
		registry.put(JsonArray.class, new JsonElementColumnMapper());
		registry.put(JsonBoolean.class, new JsonElementColumnMapper());
		registry.put(JsonNull.class, new JsonElementColumnMapper());
		registry.put(JsonNumber.class, new JsonElementColumnMapper());
		registry.put(JsonObject.class, new JsonElementColumnMapper());
		registry.put(JsonScalar.class, new JsonElementColumnMapper());
		registry.put(JsonString.class, new JsonElementColumnMapper());
	}
}
