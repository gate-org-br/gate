package gate.adapter.mapCollector;

import gate.adapter.registry.MapCollectorRegistry;
import gate.util.Reflection;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;

/** Adapts map entries to a map type. */
public interface MapCollector
{
	Map<?, ?> ofMap(Type type, Map<?, ?> values);

	static MapCollector getCollector(Class<?> type) {return MapCollectorRegistry.INSTANCE.get(type);}

	static MapCollector getCollector(Type type) {return getCollector(Reflection.getRawType(type));}

	static Map<?, ?> fromMap(Type type, Map<?, ?> values)
	{
		return getCollector(type).ofMap(type, values);
	}

	static MapCollector getCollector(Parameter parameter)
	{
		return parameter.isAnnotationPresent(gate.annotation.MapCollector.class)
				? gate.annotation.MapCollector.Extractor.extract(parameter)
				: MapCollector.getCollector(parameter.getType());
	}
}
