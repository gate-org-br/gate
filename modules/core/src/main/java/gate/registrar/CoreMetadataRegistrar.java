package gate.registrar;

import gate.converter.*;
import gate.converter.collections.*;
import gate.converter.custom.*;
import gate.lang.expression.Expression;
import gate.lang.json.*;
import gate.lang.property.metadata.*;
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

public class CoreMetadataRegistrar implements MetadataRegistrar
{
	@Override
	public void register(Map<Class<?>, Metadata> registry)
	{
		registry.put(char.class, new CharacterMetadata());
		registry.put(Character.class, new CharacterMetadata());
		registry.put(Duration.class, new DurationMetadata());
		registry.put(LocalDate.class, new LocalDateMetadata());
		registry.put(LocalDateTime.class, new LocalDateTimeMetadata());
		registry.put(LocalTime.class, new LocalTimeMetadata());
		registry.put(Pattern.class, new PatternMetadata());
		registry.put(YearMonth.class, new YearMonthMetadata());
		registry.put(String[][].class, new StringMatrixMetadata());
		registry.put(byte[].class, new ByteArrayMetadata());
		registry.put(DayOfWeek.class, new DayOfWeekMetadata());
		registry.put(File.class, new FileMetadata());
		registry.put(Month.class, new MonthMetadata());
		registry.put(Year.class, new YearMetadata());
		registry.put(Path.class, new FileMetadata());
		registry.put(EMail.class, new EMailMetadata());
		registry.put(Field.class, new FieldMetadata());
		registry.put(Form.class, new FormMetadata());
		registry.put(ID.class, new IDMetadata());
		registry.put(IMEI.class, new IMEIMetadata());
		registry.put(LocalDateInterval.class, new LocalDateIntervalMetadata());
		registry.put(LocalDateTimeInterval.class, new LocalDateTimeIntervalMetadata());
		registry.put(LocalTimeInterval.class, new LocalTimeIntervalMetadata());
		registry.put(MACAddress.class, new MACAddressMetadata());
		registry.put(Range.class, new RangeMetadata());
		registry.put(SHA256.class, new SHA256Metadata());
		registry.put(SHA512.class, new SHA512Metadata());
		registry.put(SIMCard.class, new SIMCardMetadata());
		registry.put(TempFile.class, new TempFileMetadata());
		registry.put(NamedTempFile.class, new TempFileMetadata());
		registry.put(Version.class, new VersionMetadata());
		registry.put(YearMonthInterval.class, new YearMonthIntervalMetadata());
		registry.put(BrasilianDocument.class, new BrasilianDocumentMetadata());
		registry.put(CEP.class, new CEPMetadata());
		registry.put(CNPJ.class, new CNPJMetadata());
		registry.put(CPF.class, new CPFMetadata());
		registry.put(CTPS.class, new CTPSMetadata());
		registry.put(Phone.class, new PhoneMetadata());
		registry.put(ProcessNumber.class, new ProcessNumberMetadata());
		registry.put(Renavam.class, new RenavamMetadata());
		registry.put(IntegerList.class, new IntegerListMetadata());
		registry.put(CharacterList.class, new CharacterListMetadata());
		registry.put(LocalDateTimeSet.class, new LocalDateTimeSetMetadata());
		registry.put(StringList.class, new StringListMetadata());
		registry.put(StringSet.class, new StringSetMetadata());
		registry.put(EnumSet.class, new EnumSetMetadata());
		registry.put(Data.class, new DataMetadata());
		registry.put(Expression.class, new ExpressionMetadata());
	}
}
