package gate.policonverter;

import gate.error.AppError;
import gate.error.ConversionException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Part;

public abstract class Policonverter
{

	private static final Policonverter ARRAY;
	private static final Map<String, Policonverter> POLICONVERTERS;

	static
	{
		ARRAY = new ArrayPoliconverter();
		POLICONVERTERS = new HashMap<>();
		POLICONVERTERS.put("java.util.Collection", new CollectionPoliconverter());
		POLICONVERTERS.put("java.util.Set", new SetPoliconverter());
		POLICONVERTERS.put("java.util.List", new ListPoliconverter());
		POLICONVERTERS.put("java.util.EnumSet", new EnumSetPoliconverter());
	}

	public static Policonverter getPoliconverter(Class<?> type)
	{
		try
		{
			if (!type.isArray())
			{
				Policonverter policonverter = null;

				do
				{
					if (POLICONVERTERS.get(type.getName()) == null)
						if (type.isAnnotationPresent(gate.annotation.Policonverter.class))
						{
							Class<? extends Policonverter> clazz = type.getAnnotation(gate.annotation.Policonverter.class).value();
							POLICONVERTERS.put(type.getName(), clazz.getDeclaredConstructor().newInstance());
						}

					policonverter = POLICONVERTERS.get(type.getName());
					type = type.getSuperclass();
				} while (type != null && policonverter == null);

				return policonverter;
			} else
				return ARRAY;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex)
		{
			throw new AppError(ex);
		}
	}

	public abstract Object toCollection(Class<?> type, Object[] objects);

	public abstract Object getObject(Class<?> type, String[] strings) throws ConversionException;

	public abstract String[] getString(Class<?> type, Object object) throws ConversionException;

	public abstract Object getObject(Class<?> type, Part[] parts) throws ConversionException;
}
