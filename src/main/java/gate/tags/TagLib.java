package gate.tags;

import gate.annotation.Color;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.util.Icons;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;

public class TagLib
{

	public static String icon(Object type)
	{
		return Icon.Extractor.extract(type).orElse(Icons.UNKNOWN).toString();
	}

	public static String name(Object obj)
	{
		return Name.Extractor.extract(obj).orElse("Unnamed");
	}

	public static String color(Object obj)
	{
		return Color.Extractor.extract(obj).orElse("#000000");
	}

	public static String description(Object obj)
	{
		return Description.Extractor.extract(obj).orElse("Undescribed");
	}

	public static long number(Temporal temporal)
	{
		if (temporal instanceof LocalDateTime)
			return ((LocalDateTime) temporal)
				.atZone(ZoneId.of("UTC")).toEpochSecond();
		if (temporal instanceof LocalDate)
			return ((LocalDate) temporal).atStartOfDay()
				.atZone(ZoneId.of("UTC")).toEpochSecond();
		return 0;

	}
}
