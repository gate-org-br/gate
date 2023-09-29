package gate.converter;

import gate.entity.User;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ConvertersTest
{

	@BeforeAll
	public static void setUp()
	{
		Locale.setDefault(new Locale("pt", "br"));
	}

	@Test
	public void test01()
	{
		Converter converter = Converters.INSTANCE.get(ExtendedExtendedUser.class);
		assertEquals("gate.converter.ObjectConverter", converter.getClass().getName());
	}

	public class ExtendedUser extends User
	{

	}

	public class ExtendedExtendedUser extends ExtendedUser
	{

	}

}
