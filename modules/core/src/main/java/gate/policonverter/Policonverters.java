package gate.policonverter;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;

public class Policonverters
{
	
	static final Policonverters INSTANCE = new Policonverters();
	private static final Policonverter ARRAY = new ArrayPoliconverter();
	
	private final Map<Class<?>, Policonverter> INSTANCES = new ConcurrentHashMap<>();
	
	private Policonverters()
	{
		INSTANCES.put(Collection.class, new CollectionPoliconverter());
		INSTANCES.put(Set.class, new SetPoliconverter());
		INSTANCES.put(List.class, new ListPoliconverter());
		INSTANCES.put(EnumSet.class, new EnumSetPoliconverter());
	}
	
	public Policonverter get(Class<?> type)
	{
		return type.isArray() ? ARRAY : INSTANCES.computeIfAbsent(type, e ->
		{
			try
			{
				for (Class<?> clazz = e;
					clazz != null;
					clazz = clazz.getSuperclass())
					if (INSTANCES.containsKey(clazz))
						return INSTANCES.get(clazz);
					else if (clazz.isAnnotationPresent(gate.annotation.Policonverter.class))
						return clazz.getAnnotation(gate.annotation.Policonverter.class)
							.value().getDeclaredConstructor().newInstance();
				
			} catch (InstantiationException | IllegalAccessException
				| NoSuchMethodException | InvocationTargetException ex)
			{
				LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
			}
			
			return INSTANCES.get(Object.class);
		});
	}
	
	@Override
	public String toString()
	{
		return INSTANCES.toString();
	}
}
