package gate.converter;

import gate.converter.collections.EnumSetConverter;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Converters
{

	static final Converters INSTANCE = new Converters();

	private final Map<Class<?>, Converter> INSTANCES = new ConcurrentHashMap<>();

	private Converters()
	{
		INSTANCES.put(Collection.class, new CollectionConverter());
		INSTANCES.put(Set.class, new SetConverter());
		INSTANCES.put(Map.class, new MapConverter());
		INSTANCES.put(List.class, new CollectionConverter());
		INSTANCES.put(EnumSet.class, new EnumSetConverter());

		INSTANCES.put(BigDecimal.class, new BigDecimalConverter());
		INSTANCES.put(boolean.class, new BooleanConverter());
		INSTANCES.put(Boolean.class, new BooleanConverter());
		INSTANCES.put(byte.class, new ByteConverter());
		INSTANCES.put(Byte.class, new ByteConverter());
		INSTANCES.put(char.class, new CharacterConverter());
		INSTANCES.put(Character.class, new CharacterConverter());
		INSTANCES.put(Class.class, new ClassConverter());
		INSTANCES.put(double.class, new DoubleConverter());
		INSTANCES.put(Double.class, new DoubleConverter());
		INSTANCES.put(Duration.class, new DurationConverter());
		INSTANCES.put(Enum.class, new EnumConverter());
		INSTANCES.put(float.class, new FloatConverter());
		INSTANCES.put(Float.class, new FloatConverter());
		INSTANCES.put(int.class, new IntegerConverter());
		INSTANCES.put(Integer.class, new IntegerConverter());
		INSTANCES.put(LocalDate.class, new LocalDateConverter());
		INSTANCES.put(LocalDateTime.class, new LocalDateTimeConverter());
		INSTANCES.put(LocalTime.class, new LocalTimeConverter());
		INSTANCES.put(long.class, new LongConverter());
		INSTANCES.put(Long.class, new LongConverter());
		INSTANCES.put(Number.class, new NumberConverter());
		INSTANCES.put(Pattern.class, new PatternConverter());
		INSTANCES.put(short.class, new ShortConverter());
		INSTANCES.put(Short.class, new ShortConverter());
		INSTANCES.put(String.class, new StringConverter());
		INSTANCES.put(YearMonth.class, new YearMonthConverter());
		INSTANCES.put(String[][].class, new StringMatrixConverter());
		INSTANCES.put(byte[].class, new ByteArrayConverter());
		INSTANCES.put(DayOfWeek.class, new DayOfWeekConverter());
		INSTANCES.put(File.class, new FileConverter());
		INSTANCES.put(java.time.Month.class, new MonthConverter());
		INSTANCES.put(java.time.Year.class, new YearConverter());
		INSTANCES.put(Path.class, new PathConverter());
		INSTANCES.put(java.lang.Record.class, new RecordConverter());

	}

	public Converter get(Class<?> type)
	{
		return INSTANCES.computeIfAbsent(type, e ->
		{
			try
			{
				for (Class<?> clazz = e; clazz != null; clazz = clazz.getSuperclass())
					if (INSTANCES.containsKey(clazz))
						return INSTANCES.get(clazz);
					else if (clazz.isAnnotationPresent(gate.annotation.Converter.class))
						return clazz.getAnnotation(gate.annotation.Converter.class).value().getDeclaredConstructor()
								.newInstance();
					else if (Stream.of(clazz.getInterfaces()).filter(iter -> INSTANCES.containsKey(iter)
																			 || iter.isAnnotationPresent(gate.annotation.Converter.class)).count() == 1)
						for (Class<?> inter : clazz.getInterfaces())
							if (INSTANCES.containsKey(inter))
								return INSTANCES.get(inter);
							else if (inter.isAnnotationPresent(gate.annotation.Converter.class))
								return inter.getAnnotation(gate.annotation.Converter.class).value()
										.getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
					 | InvocationTargetException ex)
			{
				LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
			}

			return new ObjectConverter();
		});
	}

	@Override
	public String toString()
	{
		return INSTANCES.toString();
	}
}