package gate.converter;

import gate.entity.User;
import gate.error.ConversionException;
import gate.type.ID;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.Test;
import gate.annotation.SecurityKey;
import java.util.Locale;
import org.junit.BeforeClass;

public class ObjectConverterTest
{

	@BeforeClass
	public static void setUp()
	{
		Locale.setDefault(new Locale("pt", "br"));
	}

	@Test
	public void test01() throws ConversionException, NoSuchAlgorithmException
	{
		User user = new User();
		user.setId(new ID(1));
		user.setName("User 1");

		Converter converter = Converter.getConverter(Object.class);

		String string = converter.toString(User.class, user);
		user = (User) converter.ofString(User.class, string);

		Assert.assertEquals(new ID(1), user.getId());
		Assert.assertEquals("User 1", user.getName());
	}

	@Test
	public void test02() throws ConversionException
	{
		ClassifiedUser user = new ClassifiedUser();
		user.setId(new ID(1));
		user.setName("User 1");

		Converter converter = Converter.getConverter(Object.class);

		String string = converter.toString(ClassifiedUser.class, user);
		user = (ClassifiedUser) converter.ofString(ClassifiedUser.class, string);

		Assert.assertEquals(new ID(1), user.getId());
		Assert.assertEquals("User 1", user.getName());

	}

	@SecurityKey("/zgq2IL4H6tN4kRrzybBQA==")
	public static class ClassifiedUser extends User
	{

	}
}
