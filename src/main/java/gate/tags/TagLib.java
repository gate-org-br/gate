package gate.tags;

import gate.annotation.Color;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Tooltip;
import gate.converter.Converter;
import gate.entity.User;
import gate.error.ConversionException;
import gate.error.UncheckedConversionEception;
import gate.util.Icons;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Pattern;
import javax.enterprise.inject.spi.CDI;

public class TagLib
{

	private static final Pattern DATE = Pattern.compile("^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4}$");
	private static final Pattern DATE_TIME = Pattern.compile("^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4} [0-9]{2}:[0-9]{2}$");

	public static String icon(Object type)
	{
		return Icon.Extractor.extract(type).orElse(Icons.UNKNOWN).toString();
	}

	public static String name(Object obj)
	{
		return Name.Extractor.extract(obj).orElse("Unnamed");
	}

	public static String tooltip(Object obj)
	{
		return Tooltip.Extractor.extract(obj).orElse("");
	}

	public static String code(Object obj)
	{
		return Tooltip.Extractor.extract(obj).orElse("");
	}

	public static String color(Object obj)
	{
		return Color.Extractor.extract(obj).orElse("#000000");
	}

	public static String description(Object obj)
	{
		return Description.Extractor.extract(obj).orElse("Undescribed");
	}

	public static long number(Object temporal)
	{
		try
		{
			if (temporal instanceof String)
				if (DATE.matcher((String) temporal).matches())
					temporal = Converter.fromString(LocalDate.class, (String) temporal);
				else if (DATE_TIME.matcher((String) temporal).matches())
					temporal = Converter.fromString(LocalDateTime.class, (String) temporal);
		} catch (ConversionException ex)
		{
			throw new UncheckedConversionEception(ex);
		}

		if (temporal instanceof LocalDateTime)
			return ((LocalDateTime) temporal)
				.atZone(ZoneId.of("UTC")).toEpochSecond();
		if (temporal instanceof LocalDate)
			return ((LocalDate) temporal).atStartOfDay()
				.atZone(ZoneId.of("UTC")).toEpochSecond();
		return 0;
	}

	public static boolean secure(String module, String screen, String action)
	{
		User user = CDI.current().select(User.class).get();
		if (user.getId() == null)
			return false;
		return user.checkAccess(module, screen, action);

	}
}
