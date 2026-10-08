package gate.adapter.collector;

import gate.adapter.registry.CollectorRegistry;
import gate.util.Reflection;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * Adapts an array of values to a collection type.
 */
public interface Collector
{
	Object ofArray(Type type, Object[] array);

	static Collector getCollector(Class<?> type) {return CollectorRegistry.INSTANCE.get(type);}

	static Collector getCollector(Type type) {return getCollector(Reflection.getRawType(type));}

	static Object fromArray(Type type, Object[] array)
	{
		return getCollector(type).ofArray(type, array);
	}

	static Collector getCollector(Parameter parameter)
	{
		return parameter.isAnnotationPresent(gate.annotation.Collector.class)
				? gate.annotation.Collector.Extractor.extract(parameter)
				: Collector.getCollector(parameter.getType());
	}
}
