package gate.type;

import org.junit.Assert;
import org.junit.Test;

public class CPFTest
{

	@Test
	public void test()
	{
		Assert.assertTrue(CPF.validate("000.000.000-00"));
		Assert.assertFalse(CPF.validate("000.000.000-01"));
	}

	@Test
	public void test2()
	{
		Assert.assertEquals("123.456.789-09", new CPF("12345678909").toString());
	}

}
