package gate.lang.json;

import gate.annotation.JsonAdapter;
import gate.converter.Converter;
import gate.error.ConversionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class JsonAdapterTest
{

	@Test
	public void shouldAdaptJsonElementOfUsingAnnotation() throws ConversionException
	{
		Assertions.assertEquals(
				new JsonObject().setString("value", "annotated"),
				JsonElement.of(new AnnotatedType("ignored")));
	}

	@Test
	public void shouldAdaptJsonObjectToObjectUsingAnnotation()
	{
		AnnotatedType value = new JsonObject().setString("value", "annotated")
				.toObject(AnnotatedType.class);

		Assertions.assertEquals("annotated", value.getValue());
	}

	@Test
	public void shouldAdaptJsonArrayElementsUsingElementTypeAdapter()
	{
		JsonArray json = new JsonArray();
		json.add(new JsonObject().setString("value", "first"));
		json.add(new JsonObject().setString("value", "second"));

		List<AnnotatedType> values = json.toObject(List.class, AnnotatedType.class);

		Assertions.assertEquals(2, values.size());
		Assertions.assertEquals("first", values.get(0).getValue());
		Assertions.assertEquals("second", values.get(1).getValue());
	}

	@Test
	public void shouldResolveJsonAdapterFromAnnotatedInterface() throws ConversionException
	{
		Assertions.assertEquals(
				new JsonObject().setString("value", "interface"),
				JsonElement.of(new InterfaceImplementation("ignored")));
	}

	@Test
	public void shouldPreferRegisteredAdapterOverAnnotation() throws ConversionException
	{
		gate.lang.json.JsonAdapter.register(RegisteredType.class, new RegisteredAdapter());

		Assertions.assertEquals(
				new JsonObject().setString("value", "registered"),
				JsonElement.of(new RegisteredType("annotated")));
	}

	@Test
	public void shouldUseJsonAdapterForJsonTextSerialization() throws ConversionException
	{
		Assertions.assertEquals("\"{\\\"value\\\":\\\"TEXT\\\"}\"", Converter.toJsonText(new TextType("text")));
	}

	@Test
	public void shouldRepresentNullAsJsonNull() throws ConversionException
	{
		Assertions.assertEquals(JsonNull.INSTANCE, JsonElement.of(null));
	}

	@JsonAdapter(AnnotatedTypeAdapter.class)
	public static final class AnnotatedType
	{
		private final String value;

		public AnnotatedType(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	public static final class AnnotatedTypeAdapter implements gate.lang.json.JsonAdapter<AnnotatedType>
	{
		@Override
		public JsonElement toJson(AnnotatedType object)
		{
			return object == null ? null : new JsonObject().setString("value", "annotated");
		}

		@Override
		public AnnotatedType fromJson(JsonElement json)
		{
			return json == null ? null : new AnnotatedType(((JsonObject) json).getString("value").orElse(null));
		}
	}

	@JsonAdapter(InterfaceTypeAdapter.class)
	public interface InterfaceType
	{
		String getValue();
	}

	public static final class InterfaceImplementation implements InterfaceType
	{
		private final String value;

		public InterfaceImplementation(String value)
		{
			this.value = value;
		}

		@Override
		public String getValue()
		{
			return value;
		}
	}

	public static final class InterfaceTypeAdapter implements gate.lang.json.JsonAdapter<InterfaceType>
	{
		@Override
		public JsonElement toJson(InterfaceType object)
		{
			return object == null ? null : new JsonObject().setString("value", "interface");
		}

		@Override
		public InterfaceType fromJson(JsonElement json)
		{
			return json == null ? null : new InterfaceImplementation(((JsonObject) json).getString("value").orElse(null));
		}
	}

	@JsonAdapter(RegisteredTypeAnnotationAdapter.class)
	public static final class RegisteredType
	{
		private final String value;

		public RegisteredType(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	public static final class RegisteredTypeAnnotationAdapter implements gate.lang.json.JsonAdapter<RegisteredType>
	{
		@Override
		public JsonElement toJson(RegisteredType object)
		{
			return object == null ? null : new JsonObject().setString("value", "annotation");
		}

		@Override
		public RegisteredType fromJson(JsonElement json)
		{
			return json == null ? null : new RegisteredType(((JsonObject) json).getString("value").orElse(null));
		}
	}

	public static final class RegisteredAdapter implements gate.lang.json.JsonAdapter<RegisteredType>
	{
		@Override
		public JsonElement toJson(RegisteredType object)
		{
			return object == null ? null : new JsonObject().setString("value", "registered");
		}

		@Override
		public RegisteredType fromJson(JsonElement json)
		{
			return json == null ? null : new RegisteredType(((JsonObject) json).getString("value").orElse(null));
		}
	}

	@JsonAdapter(TextTypeAdapter.class)
	public static final class TextType
	{
		private final String value;

		public TextType(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	public static final class TextTypeAdapter implements gate.lang.json.JsonAdapter<TextType>
	{
		@Override
		public JsonElement toJson(TextType object)
		{
			return object == null ? null : new JsonObject().setString("value", object.getValue());
		}

		@Override
		public TextType fromJson(JsonElement json)
		{
			return json == null ? null : new TextType(((JsonObject) json).getString("value").orElse(null));
		}

		@Override
		public JsonElement toJsonText(TextType object)
		{
			return object == null ? null : new JsonObject().setString("value", object.getValue().toUpperCase());
		}
	}
}
