package gate.adapter.registrar;

import gate.adapter.renderer.*;
import gate.lang.json.*;
import gate.lang.property.Property;
import gate.type.DataFile;
import gate.type.Field;
import gate.type.Form;
import gate.type.LocalDateInterval;
import gate.type.LocalDateTimeInterval;
import gate.type.LocalTimeInterval;
import gate.type.Percentage;
import gate.type.YearMonthInterval;
import gate.type.br.BrasilianDocument;
import gate.type.br.CEP;
import gate.type.br.CNPJ;
import gate.type.br.CPF;
import gate.type.br.CTPS;
import gate.type.br.Phone;
import gate.type.br.ProcessNumber;
import gate.type.br.Renavam;
import gate.type.mime.MimeText;
import gate.type.mime.MimeTextFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class CoreRendererRegistrar implements RendererRegistrar
{
	@Override
	public Map<Class<?>, Renderer> entries()
	{
		Map<Class<?>, Renderer> registry = new HashMap<>();
		registry.put(File.class, new FileRenderer());
		registry.put(boolean.class, new BooleanRenderer());
		registry.put(Boolean.class, new BooleanRenderer());
		registry.put(byte.class, new ByteRenderer());
		registry.put(Byte.class, new ByteRenderer());
		registry.put(char.class, new CharacterRenderer());
		registry.put(Character.class, new CharacterRenderer());
		registry.put(BigDecimal.class, new BigDecimalRenderer());
		registry.put(double.class, new DoubleRenderer());
		registry.put(Double.class, new DoubleRenderer());
		registry.put(float.class, new FloatRenderer());
		registry.put(Float.class, new FloatRenderer());
		registry.put(Short.class, new ShortRenderer());
		registry.put(Integer.class, new IntegerRenderer());
		registry.put(Long.class, new LongRenderer());
		registry.put(String.class, new StringRenderer());
		registry.put(DayOfWeek.class, new DayOfWeekRenderer());
		registry.put(LocalDate.class, new LocalDateRenderer());
		registry.put(LocalDateInterval.class, new LocalDateIntervalRenderer());
		registry.put(LocalDateTime.class, new LocalDateTimeRenderer());
		registry.put(LocalDateTimeInterval.class, new LocalDateTimeIntervalRenderer());
		registry.put(LocalTime.class, new LocalTimeRenderer());
		registry.put(LocalTimeInterval.class, new LocalTimeIntervalRenderer());
		registry.put(Month.class, new MonthRenderer());
		registry.put(Year.class, new YearRenderer());
		registry.put(YearMonth.class, new YearMonthRenderer());
		registry.put(YearMonthInterval.class, new YearMonthIntervalRenderer());
		registry.put(Enum.class, new EnumRenderer());
		registry.put(EnumSet.class, new EnumSetRenderer());
		registry.put(Object[].class, new ArrayRenderer());
		registry.put(Collection.class, new CollectionRenderer());
		registry.put(Percentage.class, new PercentageRenderer());
		registry.put(DataFile.class, new DataFileRenderer());
		registry.put(MimeText.class, new MimeTextRenderer());
		registry.put(MimeTextFile.class, new MimeTextFileRenderer());
		registry.put(BrasilianDocument.class, new BrasilianDocumentRenderer());
		registry.put(CEP.class, new CEPRenderer());
		registry.put(CNPJ.class, new CNPJRenderer());
		registry.put(CPF.class, new CPFRenderer());
		registry.put(CTPS.class, new CTPSRenderer());
		registry.put(Phone.class, new PhoneRenderer());
		registry.put(ProcessNumber.class, new ProcessNumberRenderer());
		registry.put(Renavam.class, new RenavamRenderer());
		registry.put(JsonElement.class, new JsonElementRenderer());
		registry.put(JsonArray.class, new JsonElementRenderer());
		registry.put(JsonBoolean.class, new JsonElementRenderer());
		registry.put(JsonNull.class, new JsonElementRenderer());
		registry.put(JsonNumber.class, new JsonElementRenderer());
		registry.put(JsonObject.class, new JsonElementRenderer());
		registry.put(JsonScalar.class, new JsonElementRenderer());
		registry.put(JsonString.class, new JsonElementRenderer());
		registry.put(Field.class, new FieldRenderer());
		registry.put(Form.class, new FormRenderer());
		registry.put(Property.class, new PropertyRenderer());
		registry.put(Duration.class, new DurationRenderer());
		return registry;
	}
}
