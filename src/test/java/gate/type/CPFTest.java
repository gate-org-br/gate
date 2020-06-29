package gate.type;

import org.junit.Assert;
import org.junit.Test;

public class CPFTest
{

	@Test
	public void testValidate1()
	{
		Assert.assertTrue(CPF.validate("314.343.884-33"));
	}

	@Test
	public void testValidate2()
	{
		Assert.assertTrue(CPF.validate("977.234.477-79"));
	}

	@Test
	public void testValidate3()
	{
		Assert.assertFalse(CPF.validate("000.000.000-01"));
	}

	@Test
	public void testToFormat1()
	{
		Assert.assertNull(CPF.format("0"));
	}

	@Test
	public void testToFormat2()
	{
		Assert.assertEquals("000.000.000-00", CPF.format("00"));
	}

	@Test
	public void testToFormat3()
	{
		Assert.assertEquals("000.000.000-00", CPF.format("000"));
	}

	@Test
	public void testToFormat4()
	{
		Assert.assertEquals("000.000.000-00", CPF.format("0000"));
	}

	@Test
	public void testToFormat5()
	{
		Assert.assertEquals("000.000.000-00", CPF.format("00000"));
	}

	@Test
	public void testToFormat6()
	{
		Assert.assertEquals("000.000.000-00", CPF.format("000000"));
	}

	@Test
	public void testToFormat7()
	{
		Assert.assertEquals("000.000.000-00", CPF.format("0000000"));
	}

	@Test
	public void testToFormat8()
	{
		Assert.assertEquals("000.000.000-00", CPF.format("00000000"));
	}

	@Test
	public void testToFormat9()
	{
		Assert.assertEquals("000.000.000-00", CPF.format("000000000"));
	}

	@Test
	public void testToFormat10()
	{
		Assert.assertEquals("000.000.000-00", CPF.format("0000000000"));
	}

	@Test
	public void testToFormat11()
	{
		Assert.assertEquals("000.000.000-00", CPF.format("00000000000"));
	}

	@Test
	public void testToFormat14()
	{
		Assert.assertEquals("000.000.000-00", CPF.format("000.000.000-00"));
	}
}
