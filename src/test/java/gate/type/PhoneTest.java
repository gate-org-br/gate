package gate.type;

import org.junit.Assert;
import org.junit.Test;

public class PhoneTest
{

	public PhoneTest()
	{
	}

	@Test
	public void test01()
	{
		Phone phone = new Phone("0317130118168");
		Assert.assertEquals("31", phone.getProvider());
		Assert.assertEquals("71", phone.getDDD());
		Assert.assertEquals("30118168", phone.getNumber());
	}

	@Test
	public void test02()
	{
		Phone phone = new Phone("7130118168");
		Assert.assertNull(phone.getProvider());
		Assert.assertEquals("71", phone.getDDD());
		Assert.assertEquals("30118168", phone.getNumber());
	}

	@Test
	public void test03()
	{
		Phone phone = new Phone("03171988958168");
		Assert.assertEquals("31", phone.getProvider());
		Assert.assertEquals("71", phone.getDDD());
		Assert.assertEquals("988958168", phone.getNumber());
	}
}
