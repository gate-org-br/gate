package gate.type;

import org.junit.Assert;
import org.junit.Test;

public class CNPJTest
{

	@Test
	public void test()
	{
		Assert.assertTrue(CNPJ.validate("00000000/0001-91"));
		Assert.assertFalse(CNPJ.validate("00000000/0001-92"));
	}

	@Test
	public void test2()
	{
		Assert.assertEquals("00.000.000/0001-91", new CNPJ("00000000000191").toString());
	}
}
