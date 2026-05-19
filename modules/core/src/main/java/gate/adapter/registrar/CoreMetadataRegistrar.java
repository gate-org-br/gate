package gate.adapter.registrar;

import gate.adapter.metadata.*;
import gate.lang.expression.Expression;
import gate.type.*;
import gate.type.br.*;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CoreMetadataRegistrar implements MetadataRegistrar
{
	@Override
	public Map<Class<?>, Metadata> entries()
	{
		Map<Class<?>, Metadata> registry = new HashMap<>();
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
		registry.put(EnumSet.class, new EnumSetMetadata());
		registry.put(Data.class, new DataMetadata());
		registry.put(Expression.class, new ExpressionMetadata());
		registry.put(BigDecimal.class, new BigDecimalMetadata());
		return registry;
	}
}