package gate.adapter.registrar;


import gate.adapter.jsonRenderer.*;
import gate.lang.json.*;

import java.time.*;
import java.util.*;

public class CoreJsonRendererRegistrar implements JsonRendererRegistrar
{
	@Override
	public Map<Class<?>, JsonRenderer> entries()
	{
		Map<Class<?>, JsonRenderer> registry = new HashMap<>();

		var defaultRenderer = new DefaultJsonRenderer();
		registry.put(String.class, defaultRenderer);
		registry.put(boolean.class, defaultRenderer);
		registry.put(Boolean.class, defaultRenderer);
		registry.put(char.class, defaultRenderer);
		registry.put(Character.class, defaultRenderer);
		registry.put(Number.class, defaultRenderer);
		registry.put(Enum.class, new EnumJsonRenderer());
		registry.put(DayOfWeek.class, new DayOfWeekJsonRenderer());
		registry.put(Month.class, new MonthJsonRenderer());
		registry.put(Duration.class, defaultRenderer);
		registry.put(LocalDate.class, defaultRenderer);
		registry.put(LocalDateTime.class, defaultRenderer);
		registry.put(LocalTime.class, defaultRenderer);
		registry.put(Year.class, defaultRenderer);
		registry.put(YearMonth.class, defaultRenderer);

		registry.put(Collection.class, new CollectionJsonRenderer());
		registry.put(List.class, new CollectionJsonRenderer());
		registry.put(Set.class, new CollectionJsonRenderer());
		registry.put(Map.class, new MapJsonRenderer());

		registry.put(JsonElement.class, defaultRenderer);
		registry.put(JsonArray.class, defaultRenderer);
		registry.put(JsonBoolean.class, defaultRenderer);
		registry.put(JsonNull.class, defaultRenderer);
		registry.put(JsonNumber.class, defaultRenderer);
		registry.put(JsonObject.class, defaultRenderer);
		registry.put(JsonScalar.class, defaultRenderer);
		registry.put(JsonString.class, defaultRenderer);

		return registry;
	}
}