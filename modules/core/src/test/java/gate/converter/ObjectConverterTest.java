package gate.converter;

import gate.adapter.converter.Converter;
import gate.annotation.JsonAdapter;
import gate.error.ConversionException;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import mock.IDMock;
import mock.RoleMock;
import mock.UserMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ObjectConverterTest
{
	@Test
	public void shouldRoundTripObjectConversion() throws ConversionException
	{
		UserMock user = new UserMock();
		user.setId(1);
		user.setName("Users 1");

		Converter converter = Converter.getConverter(Object.class);

		String string = converter.toString(UserMock.class, user);
		user = (UserMock) converter.ofString(UserMock.class, string);

		assertEquals(1, user.getId());
		assertEquals("Users 1", user.getName());
	}

	@Test
	public void shouldRoundTripSecureObjectConversion() throws ConversionException
	{
		UserMock user = new UserMock();
		user.setId(1);
		user.setName("Users 1");

		Converter converter = Converter.getConverter(Object.class);

		String string = converter.toString(UserMock.class, user);
		user = (UserMock) converter.ofString(UserMock.class, string);

		assertEquals(1, user.getId());
		assertEquals("Users 1", user.getName());

	}

	@Test
	public void shouldConvertNullToEmptyStringAndBack() throws ConversionException
	{
		Converter converter = Converter.getConverter(Object.class);
		assertEquals("", converter.toString(Object.class, null));
		assertNull(converter.ofString(Object.class, ""));
	}

	@Test
	public void shouldUseValueOfStringAsObjectTextConversion() throws ConversionException
	{
		Converter converter = Converter.getConverter(ValueOfObject.class);
		ValueOfObject object = new ValueOfObject("test");

		assertEquals("test", converter.toString(ValueOfObject.class, object));
		assertEquals(object, converter.ofString(ValueOfObject.class, "test"));
	}

	@Test
	public void shouldNotUseOfStringAsObjectTextConversion() throws ConversionException
	{
		Converter converter = Converter.getConverter(Object.class);
		OfObject object = new OfObject("test");

		String string = converter.toString(OfObject.class, object);

		Assertions.assertNotEquals("test", string);
		assertEquals(object, converter.ofString(OfObject.class, string));
	}

	@Test
	public void shouldSkipCircularReferences() throws ConversionException
	{
		UserMock user = new UserMock().setId(1);
		RoleMock role = new RoleMock().setId(IDMock.valueOf(2));
		user.setRole(role);
		role.setManager(user);
		Assertions.assertEquals("{\"role\":{\"manager\":{\"level\":0,\"id\":1},\"id\":\"2\"},\"level\":0,\"id\":1}",
				Converter.toJson(user));
	}

	@Test
	public void shouldUseJsonAdapterDuringObjectJsonSerialization()
	{
		Assertions.assertEquals("{\"value\":\"adapted\"}", Converter.toJson(new AdaptedObject("ignored")));
	}

	@Test
	public void shouldUseJsonAdapterDuringObjectJsonParsing() throws ConversionException
	{
		AdaptedObject object = Converter.fromJson(AdaptedObject.class, "{\"value\":\"adapted\"}");
		Assertions.assertEquals("adapted", object.getValue());
	}

	@Test
	public void shouldNotConsumeFollowingPropertiesWhenUsingJsonAdapter() throws ConversionException
	{
		Envelope envelope = Converter.fromJson(Envelope.class,
				"{\"adapted\":{\"value\":\"adapted\"},\"next\":\"tail\"}");
		Assertions.assertEquals("adapted", envelope.getAdapted().getValue());
		Assertions.assertEquals("tail", envelope.getNext());
	}

	@JsonAdapter(AdaptedObjectJsonAdapter.class)
	public static final class AdaptedObject
	{
		private final String value;

		private AdaptedObject(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	public static final class Envelope
	{
		private AdaptedObject adapted;
		private String next;

		public AdaptedObject getAdapted()
		{
			return adapted;
		}

		public void setAdapted(AdaptedObject adapted)
		{
			this.adapted = adapted;
		}

		public String getNext()
		{
			return next;
		}

		public void setNext(String next)
		{
			this.next = next;
		}
	}

	public record ValueOfObject(String value)
	{
		public static ValueOfObject valueOf(String value)
		{
			return new ValueOfObject(value);
		}

		@Override
		public String toString()
		{
			return value;
		}
	}

	public record OfObject(String value)
	{
		public static OfObject of(String value)
		{
			return new OfObject(value);
		}

		@Override
		public String toString()
		{
			return value;
		}
	}

	public static final class AdaptedObjectJsonAdapter implements gate.lang.json.JsonAdapter<AdaptedObject>
	{
		@Override
		public JsonElement toJson(AdaptedObject object)
		{
			return object == null ? null : new JsonObject().setString("value", "adapted");
		}

		@Override
		public AdaptedObject fromJson(JsonElement json)
		{
			return json == null ? null : new AdaptedObject(((JsonObject) json).getString("value").orElse(null));
		}
	}
}