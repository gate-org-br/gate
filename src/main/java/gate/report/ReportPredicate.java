package gate.report;

import gate.converter.Converter;
import gate.error.ConversionException;
import gate.error.UncheckedConversionException;
import gate.lang.json.JsonBoolean;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNumber;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Predicate;

public class ReportPredicate implements Predicate<Object>
{

	private final String type;
	private final JsonElement element;

	public ReportPredicate(String type, JsonElement element)
	{
		this.type = type;
		this.element = element;
	}

	public static ReportPredicate of(JsonObject object)
	{
		return new ReportPredicate(object.getString("type").orElseThrow(() -> new IllegalArgumentException("Predicate type not specified")),
				object.getJsonElement("value").orElseThrow(() -> new IllegalArgumentException("Predicate value not specified")));
	}

	@Override
	public boolean test(Object value)
	{
		try
		{
			return switch (type)
			{
				case "eq" ->
					eq(value);
				case "gt" ->
					gt(value);
				case "ge" ->
					ge(value);
				case "lt" ->
					lt(value);
				case "le" ->
					le(value);
				case "rx" ->
					rx(value);
				default ->
					throw new IllegalArgumentException(type + " is not a valid type.");
			};
		} catch (ConversionException ex)
		{
			throw new UncheckedConversionException(ex);
		}
	}

	private boolean eq(Object object) throws ConversionException
	{
		if (object instanceof Number value)
			return element instanceof JsonNumber number
					&& value.doubleValue() == number.doubleValue();

		if (object instanceof String value)
			return element instanceof JsonString string
					&& value.compareTo(string.getValue()) == 0;

		if (object instanceof Boolean value)
			return element instanceof JsonBoolean bool
					&& value.compareTo(bool.getValue()) == 0;

		if (object instanceof LocalDate value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalDate.class).ofString(LocalDate.class, string.getValue()) instanceof LocalDate date
					&& value.isEqual(date);

		if (object instanceof LocalTime value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalTime.class).ofString(LocalTime.class, string.getValue()) instanceof LocalTime time
					&& value.equals(time);

		if (object instanceof LocalDateTime value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalDateTime.class).ofString(LocalDateTime.class, string.getValue()) instanceof LocalDateTime dateTime
					&& value.isEqual(dateTime);

		return false;
	}

	private boolean gt(Object object) throws ConversionException
	{
		if (object instanceof Number value)
			return element instanceof JsonNumber number
					&& value.doubleValue() > number.doubleValue();

		if (object instanceof String value)
			return element instanceof JsonString string
					&& value.compareTo(string.getValue()) > 0;

		if (object instanceof Boolean value)
			return element instanceof JsonBoolean bool
					&& value.compareTo(bool.getValue()) > 0;

		if (object instanceof LocalDate value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalDate.class).ofString(LocalDate.class, string.getValue()) instanceof LocalDate date
					&& value.isAfter(date);

		if (object instanceof LocalTime value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalTime.class).ofString(LocalTime.class, string.getValue()) instanceof LocalTime time
					&& value.isAfter(time);

		if (object instanceof LocalDateTime value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalDateTime.class).ofString(LocalDateTime.class, string.getValue()) instanceof LocalDateTime dateTime
					&& value.isAfter(dateTime);

		return false;
	}

	private boolean ge(Object object) throws ConversionException
	{
		if (object instanceof Number value)
			return element instanceof JsonNumber number
					&& value.doubleValue() >= number.doubleValue();

		if (object instanceof String value)
			return element instanceof JsonString string
					&& value.compareTo(string.getValue()) >= 0;

		if (object instanceof Boolean value)
			return element instanceof JsonBoolean bool
					&& value.compareTo(bool.getValue()) >= 0;

		if (object instanceof LocalDate value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalDate.class).ofString(LocalDate.class, string.getValue()) instanceof LocalDate date
					&& (value.isEqual(date) || value.isAfter(date));

		if (object instanceof LocalTime value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalTime.class).ofString(LocalTime.class, string.getValue()) instanceof LocalTime time
					&& (value.equals(time) || value.isAfter(time));

		if (object instanceof LocalDateTime value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalDateTime.class).ofString(LocalDateTime.class, string.getValue()) instanceof LocalDateTime dateTime
					&& (value.isEqual(dateTime) || value.isAfter(dateTime));

		return false;
	}

	private boolean lt(Object object) throws ConversionException
	{
		if (object instanceof Number value)
			return element instanceof JsonNumber number
					&& value.doubleValue() < number.doubleValue();

		if (object instanceof String value)
			return element instanceof JsonString string
					&& value.compareTo(string.getValue()) < 0;

		if (object instanceof Boolean value)
			return element instanceof JsonBoolean bool
					&& value.compareTo(bool.getValue()) < 0;

		if (object instanceof LocalDate value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalDate.class).ofString(LocalDate.class, string.getValue()) instanceof LocalDate date
					&& value.isBefore(date);

		if (object instanceof LocalTime value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalTime.class).ofString(LocalTime.class, string.getValue()) instanceof LocalTime time
					&& value.isBefore(time);

		if (object instanceof LocalDateTime value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalDateTime.class).ofString(LocalDateTime.class, string.getValue()) instanceof LocalDateTime dateTime
					&& value.isBefore(dateTime);

		return false;
	}

	private boolean le(Object object) throws ConversionException
	{
		if (object instanceof Number value)
			return element instanceof JsonNumber number
					&& value.doubleValue() <= number.doubleValue();

		if (object instanceof String value)
			return element instanceof JsonString string
					&& value.compareTo(string.getValue()) <= 0;

		if (object instanceof Boolean value)
			return element instanceof JsonBoolean bool
					&& value.compareTo(bool.getValue()) <= 0;

		if (object instanceof LocalDate value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalDate.class).ofString(LocalDate.class, string.getValue()) instanceof LocalDate date
					&& (value.isEqual(date) || value.isBefore(date));

		if (object instanceof LocalTime value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalTime.class).ofString(LocalTime.class, string.getValue()) instanceof LocalTime time
					&& (value.equals(time) || value.isBefore(time));

		if (object instanceof LocalDateTime value)
			return element instanceof JsonString string
					&& Converter.getConverter(LocalDateTime.class).ofString(LocalDateTime.class, string.getValue()) instanceof LocalDateTime dateTime
					&& (value.isEqual(dateTime) || value.isBefore(dateTime));

		return false;
	}

	private boolean rx(Object object) throws ConversionException
	{

		if (object instanceof String value)
			return element instanceof JsonString string
					&& value.matches(string.getValue());

		return false;
	}
}
