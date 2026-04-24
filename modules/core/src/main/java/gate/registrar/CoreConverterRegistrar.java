package gate.registrar;

import gate.converter.*;
import gate.converter.collections.*;
import gate.converter.custom.*;
import gate.lang.json.*;
import gate.type.*;
import gate.type.br.*;
import gate.type.collections.*;
import gate.type.mime.MimeData;
import gate.type.mime.MimeDataFile;
import gate.type.mime.MimeText;
import gate.type.mime.MimeTextFile;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.regex.Pattern;

public class CoreConverterRegistrar implements ConverterRegistrar
{

	@Override
	public void register(Map<Class<?>, Converter> registry)
	{
		registry.put(Collection.class, new CollectionConverter());
		registry.put(Set.class, new SetConverter());
		registry.put(Map.class, new MapConverter());
		registry.put(List.class, new CollectionConverter());
		registry.put(EnumSet.class, new EnumSetConverter());

		registry.put(BigDecimal.class, new BigDecimalConverter());
		registry.put(boolean.class, new BooleanConverter());
		registry.put(Boolean.class, new BooleanConverter());
		registry.put(byte.class, new ByteConverter());
		registry.put(Byte.class, new ByteConverter());
		registry.put(char.class, new CharacterConverter());
		registry.put(Character.class, new CharacterConverter());
		registry.put(Class.class, new ClassConverter());
		registry.put(double.class, new DoubleConverter());
		registry.put(Double.class, new DoubleConverter());
		registry.put(Duration.class, new DurationConverter());
		registry.put(Enum.class, new EnumConverter());
		registry.put(float.class, new FloatConverter());
		registry.put(Float.class, new FloatConverter());
		registry.put(int.class, new IntegerConverter());
		registry.put(Integer.class, new IntegerConverter());
		registry.put(LocalDate.class, new LocalDateConverter());
		registry.put(LocalDateTime.class, new LocalDateTimeConverter());
		registry.put(LocalTime.class, new LocalTimeConverter());
		registry.put(long.class, new LongConverter());
		registry.put(Long.class, new LongConverter());
		registry.put(Number.class, new NumberConverter());
		registry.put(Pattern.class, new PatternConverter());
		registry.put(short.class, new ShortConverter());
		registry.put(Short.class, new ShortConverter());
		registry.put(String.class, new StringConverter());
		registry.put(YearMonth.class, new YearMonthConverter());
		registry.put(String[][].class, new StringMatrixConverter());
		registry.put(byte[].class, new ByteArrayConverter());
		registry.put(DayOfWeek.class, new DayOfWeekConverter());
		registry.put(File.class, new FileConverter());
		registry.put(Month.class, new MonthConverter());
		registry.put(Year.class, new YearConverter());
		registry.put(Path.class, new PathConverter());

		registry.put(DataGrid.class, new DataGridConverter());
		registry.put(Data.class, new DataConverter());
		registry.put(DataFile.class, new DataFileConverter());
		registry.put(EMail.class, new EMailConverter());
		registry.put(Field.class, new FieldConverter());
		registry.put(Form.class, new FormConverter());
		registry.put(ID.class, new IDConverter());
		registry.put(IDS.class, new IDSConverter());
		registry.put(IMEI.class, new IMEIConverter());
		registry.put(LocalDateInterval.class, new LocalDateIntervalConverter());
		registry.put(LocalDateTimeInterval.class, new LocalDateTimeIntervalConverter());
		registry.put(LocalTimeInterval.class, new LocalTimeIntervalConverter());
		registry.put(MACAddress.class, new MACAddressConverter());
		registry.put(Money.class, new MoneyConverter());
		registry.put(Percentage.class, new PercentageConverter());
		registry.put(PNG.class, new PNGConverter());
		registry.put(Range.class, new RangeConverter());
		registry.put(Result.class, new ResultConverter());
		registry.put(SHA256.class, new SHA256Converter());
		registry.put(SHA512.class, new SHA512Converter());
		registry.put(SIMCard.class, new SIMCardConverter());
		registry.put(Tax.class, new TaxConverter());
		registry.put(TempFile.class, new TempFileConverter());
		registry.put(NamedTempFile.class, new TempFileConverter());
		registry.put(Version.class, new VersionConverter());
		registry.put(YearMonthInterval.class, new YearMonthIntervalConverter());

		registry.put(BrasilianDocument.class, new BrasilianDocumentConverter());
		registry.put(CEP.class, new CEPConverter());
		registry.put(CNPJ.class, new CNPJConverter());
		registry.put(CPF.class, new CPFConverter());
		registry.put(CTPS.class, new CTPSConverter());
		registry.put(Phone.class, new PhoneConverter());
		registry.put(ProcessNumber.class, new ProcessNumberConverter());
		registry.put(Renavam.class, new RenavamConverter());

		registry.put(MimeDataFile.class, new MimeDataFileConverter());
		registry.put(MimeTextFile.class, new MimeTextFileConverter());
		registry.put(MimeText.class, new MimeTextConverter());
		registry.put(MimeData.class, new MimeDataConverter());

		registry.put(JsonElement.class, new JsonElementConverter());
		registry.put(JsonArray.class, new JsonElementConverter());
		registry.put(JsonBoolean.class, new JsonElementConverter());
		registry.put(JsonNull.class, new JsonElementConverter());
		registry.put(JsonNumber.class, new JsonElementConverter());
		registry.put(JsonObject.class, new JsonElementConverter());
		registry.put(JsonScalar.class, new JsonElementConverter());
		registry.put(JsonString.class, new JsonElementConverter());

		registry.put(StringList.class, new StringListConverter());
		registry.put(CharacterList.class, new CharacterListConverter());
		registry.put(IntegerList.class, new IntegerListConverter());
		registry.put(LocalDateTimeSet.class, new LocalDateTimeSetConverter());
		registry.put(StringSet.class, new StringSetConverter());
	}
}