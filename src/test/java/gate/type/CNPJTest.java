package gate.type;

import org.junit.Assert;
import org.junit.Test;

public class CNPJTest
{

	@Test
	public void testValidate1()
	{
		Assert.assertTrue(CNPJ.validate("69.362.335/0001-56"));
	}

	@Test
	public void testValidate2()
	{
		Assert.assertTrue(CNPJ.validate("44.733.243/0001-04"));
	}

	@Test
	public void testValidate3()
	{
		Assert.assertTrue(CNPJ.validate("00.000.000/0001-91"));
	}

	@Test
	public void testValidate4()
	{
		Assert.assertFalse(CNPJ.validate("44.733.243/0001-98"));
	}

	@Test
	public void testToFormat1()
	{
		Assert.assertNull(CNPJ.format("0"));
	}

	@Test
	public void testToFormat2()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("00"));
	}

	@Test
	public void testToFormat3()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("000"));
	}

	@Test
	public void testToFormat4()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("0000"));
	}

	@Test
	public void testToFormat5()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("00000"));
	}

	@Test
	public void testToFormat6()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("000000"));
	}

	@Test
	public void testToFormat7()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("0000000"));
	}

	@Test
	public void testToFormat8()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("00000000"));
	}

	@Test
	public void testToFormat9()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("000000000"));
	}

	@Test
	public void testToFormat10()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("0000000000"));
	}

	@Test
	public void testToFormat11()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("00000000000"));
	}

	@Test
	public void testToFormat12()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("000000000000"));
	}

	@Test
	public void testToFormat13()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("0000000000000"));
	}

	@Test
	public void testToFormat14()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("00000000000000"));
	}

	@Test
	public void testToFormat18()
	{
		Assert.assertEquals("00.000.000/0000-00", CNPJ.format("00.000.000/0000-00"));
	}
}
