package gate.converter;

import gate.adapter.converter.Converter;
import mock.UserMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
		Converter converter = Converter.getConverter(ExtendedExtendedUser.class);
		assertEquals("gate.adapter.converter.ObjectConverter", converter.getClass().getName());
	}

	public static class ExtendedUser extends UserMock
	{

	}

	public static class ExtendedExtendedUser extends ExtendedUser
	{

	}

}