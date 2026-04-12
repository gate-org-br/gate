package gate.converter;

import gate.annotation.JsonAdapter;
import gate.entity.Role;
import gate.entity.User;
import gate.error.ConversionException;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.type.ID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ObjectConverterTest
{

	@Test
	public void shouldRoundTripObjectConversion() throws ConversionException
	{
		User user = new User();
		user.setId(ID.valueOf(1));
		user.setName("Users 1");

		Converter converter = Converter.getConverter(Object.class);

		String string = converter.toString(User.class, user);
		user = (User) converter.ofString(User.class, string);

		assertEquals(ID.valueOf(1), user.getId());
		assertEquals("Users 1", user.getName());
	}

	@Test
	public void shouldRoundTripSecureObjectConversion() throws ConversionException
	{
		User user = new User();
		user.setId(ID.valueOf(1));
		user.setName("Users 1");

		Converter converter = Converter.getConverter(Object.class);

		String string = converter.toString(User.class, user);
		user = (User) converter.ofString(User.class, string);

		assertEquals(ID.valueOf(1), user.getId());
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
	public void shouldSkipCircularReferences() throws ConversionException
	{
		User user = new User().setId(ID.valueOf(1));
		Role role = new Role().setId(ID.valueOf(2));
		user.setRole(role);
		role.setManager(user);
		Assertions.assertEquals("{\"role\":{\"manager\":{\"id\":\"0000000001\"},\"id\":\"0000000002\"},\"id\":\"0000000001\"}",
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