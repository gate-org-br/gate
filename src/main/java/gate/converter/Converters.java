package gate.converter;

import gate.error.AppError;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class Converters
{

	static final Converters INSTANCE = new Converters();

	private final Map<Class<?>, Converter> INSTANCES = new ConcurrentHashMap<>();

	private Converters()
	{

		INSTANCES.put(AbstractCollection.class, new CollectionConverter());
		INSTANCES.put(AbstractSet.class, new SetConverter());
		INSTANCES.put(Set.class, new SetConverter());
		INSTANCES.put(BigDecimal.class, new BigDecimalConverter());
		INSTANCES.put(boolean.class, new BooleanConverter());
		INSTANCES.put(Boolean.class, new BooleanConverter());
		INSTANCES.put(byte.class, new ByteConverter());
		INSTANCES.put(Byte.class, new ByteConverter());
		INSTANCES.put(char.class, new CharacterConverter());
		INSTANCES.put(Character.class, new CharacterConverter());
		INSTANCES.put(Class.class, new ClassConverter());
		INSTANCES.put(Collection.class, new CollectionConverter());
		INSTANCES.put(double.class, new DoubleConverter());
		INSTANCES.put(Double.class, new DoubleConverter());
		INSTANCES.put(Duration.class, new DurationConverter());
		INSTANCES.put(Enum.class, new EnumConverter());
		INSTANCES.put(float.class, new FloatConverter());
		INSTANCES.put(Float.class, new FloatConverter());
		INSTANCES.put(int.class, new IntegerConverter());
		INSTANCES.put(Integer.class, new IntegerConverter());
		INSTANCES.put(List.class, new CollectionConverter());
		INSTANCES.put(LocalDate.class, new LocalDateConverter());
		INSTANCES.put(LocalDateTime.class, new LocalDateTimeConverter());
		INSTANCES.put(LocalTime.class, new LocalTimeConverter());
		INSTANCES.put(long.class, new LongConverter());
		INSTANCES.put(Long.class, new LongConverter());
		INSTANCES.put(Number.class, new NumberConverter());
		INSTANCES.put(Object.class, new ObjectConverter());
		INSTANCES.put(Pattern.class, new PatternConverter());
		INSTANCES.put(short.class, new ShortConverter());
		INSTANCES.put(Short.class, new ShortConverter());
		INSTANCES.put(String.class, new StringConverter());
		INSTANCES.put(YearMonth.class, new YearMonthConverter());
		INSTANCES.put(String[][].class, new StringMatrixConverter());
		INSTANCES.put(byte[].class, new ByteArrayConverter());
	}

	public Converter get(Class<?> type)
	{
		return INSTANCES.computeIfAbsent(type, e ->
		{
			try
			{
				if (e.isAnnotationPresent(gate.annotation.Converter.class))
					return e.getAnnotation(gate.annotation.Converter.class)
						.value().getDeclaredConstructor().newInstance();

				Class<?> supertype = e.getSuperclass();
				if (supertype == null)
					supertype = Object.class;
				return get(supertype);
			} catch (InstantiationException | IllegalAccessException
				| NoSuchMethodException | InvocationTargetException ex)
			{
				throw new AppError(ex);
			}
		});
	}

	@Override
	public String toString()
	{
		return INSTANCES.toString();
	}
}
