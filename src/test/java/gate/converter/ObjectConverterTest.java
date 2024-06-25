package gate.converter;

import gate.entity.User;
import gate.error.ConversionException;
import gate.type.ID;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ObjectConverterTest
{

	@BeforeAll
	public static void setUp()
	{
		Locale.setDefault(new Locale("pt", "br"));
	}

	@Test
	public void testObject() throws ConversionException, NoSuchAlgorithmException
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
	public void testSecureObject() throws ConversionException
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
	public void testNull() throws ConversionException
	{
		Converter converter = Converter.getConverter(Object.class);
		assertEquals("", converter.toString(Object.class, null));
		assertNull(converter.ofString(Object.class, ""));
	}

}
