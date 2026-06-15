package gate.adapter.registrar;

import gate.adapter.jsonConverter.*;
import gate.lang.json.*;
import gate.type.DataGrid;
import gate.type.Form;

import java.io.File;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.regex.Pattern;

public class CoreJsonConverterRegistrar implements JsonConverterRegistrar
{
	@Override
	public Map<Class<?>, JsonConverter> entries()
	{
		Map<Class<?>, JsonConverter> registry = new HashMap<>();

		registry.put(String.class, new StringJsonConverter());

		registry.put(boolean.class, new BooleanJsonConverter());
		registry.put(Boolean.class, new BooleanJsonConverter());
		registry.put(char.class, new CharacterJsonConverter());
		registry.put(Character.class, new CharacterJsonConverter());
		registry.put(Class.class, new ClassJsonConverter());
		registry.put(Pattern.class, new PatternJsonConverter());
		registry.put(Path.class, new PathJsonConverter());
		registry.put(Enum.class, new EnumJsonConverter());

		var number = new NumberJsonConverter();
		registry.put(byte.class, number);
		registry.put(short.class, number);
		registry.put(int.class, number);
		registry.put(long.class, number);
		registry.put(float.class, number);
		registry.put(double.class, number);
		registry.put(Number.class, number);

		var unsupported = new UnsupportedTypeJsonConverter();
		registry.put(File.class, unsupported);

		registry.put(Duration.class, new DurationJsonConverter());
		registry.put(LocalDate.class, new LocalDateJsonConverter());
		registry.put(LocalDateTime.class, new LocalDateTimeJsonConverter());
		registry.put(LocalTime.class, new LocalTimeJsonConverter());
		registry.put(Year.class, new YearJsonConverter());
		registry.put(YearMonth.class, new YearMonthJsonConverter());

		registry.put(Object[].class, new ArrayJsonConverter());
		registry.put(Collection.class, new CollectionJsonConverter());
		registry.put(Map.class, new MapJsonConverter());

		registry.put(JsonElement.class, new JsonElementJsonConverter());
		registry.put(JsonArray.class, new JsonElementJsonConverter());
		registry.put(JsonBoolean.class, new JsonElementJsonConverter());
		registry.put(JsonNull.class, new JsonElementJsonConverter());
		registry.put(JsonNumber.class, new JsonElementJsonConverter());
		registry.put(JsonObject.class, new JsonElementJsonConverter());
		registry.put(JsonScalar.class, new JsonElementJsonConverter());
		registry.put(JsonString.class, new JsonElementJsonConverter());

		registry.put(Form.class, new FormJsonConverter());
		registry.put(DataGrid.class, new DataGridJsonConverter());

		return registry;
	}
}
