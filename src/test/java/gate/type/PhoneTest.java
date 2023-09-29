package gate.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class PhoneTest
{

	public PhoneTest()
	{
	}

	@Test
	public void test01()
	{
		Phone phone = new Phone("0317130118168");
		assertEquals("31", phone.getProvider());
		assertEquals("71", phone.getDDD());
		assertEquals("30118168", phone.getNumber());
	}

	@Test
	public void test02()
	{
		Phone phone = new Phone("7130118168");
		assertNull(phone.getProvider());
		assertEquals("71", phone.getDDD());
		assertEquals("30118168", phone.getNumber());
	}

	@Test
	public void test03()
	{
		Phone phone = new Phone("03171988958168");
		assertEquals("31", phone.getProvider());
		assertEquals("71", phone.getDDD());
		assertEquals("988958168", phone.getNumber());
	}
}
