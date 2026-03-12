package gate.converter;

import gate.entity.Role;
import gate.entity.User;
import gate.error.ConversionException;
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
		user.setName("User 1");

		Converter converter = Converter.getConverter(Object.class);

		String string = converter.toString(User.class, user);
		user = (User) converter.ofString(User.class, string);

		assertEquals(ID.valueOf(1), user.getId());
		assertEquals("User 1", user.getName());
	}

	@Test
	public void shouldRoundTripSecureObjectConversion() throws ConversionException
	{
		User user = new User();
		user.setId(ID.valueOf(1));
		user.setName("User 1");

		Converter converter = Converter.getConverter(Object.class);

		String string = converter.toString(User.class, user);
		user = (User) converter.ofString(User.class, string);

		assertEquals(ID.valueOf(1), user.getId());
		assertEquals("User 1", user.getName());

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
}