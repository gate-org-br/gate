package gate.converter;

import gate.entity.User;
import java.util.Locale;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConvertersTest
{

	@BeforeClass
	public static void setUp()
	{
		Locale.setDefault(new Locale("pt", "br"));
	}

	@Test
	public void test01()
	{
		Converter converter = Converters.INSTANCE.get(ExtendedExtendedUser.class);
		Assert.assertEquals("gate.converter.ObjectConverter", converter.getClass().getName());
	}

	public class ExtendedUser extends User
	{

	}

	public class ExtendedExtendedUser extends ExtendedUser
	{

	}

}
