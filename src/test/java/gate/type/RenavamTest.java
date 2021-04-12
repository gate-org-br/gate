package gate.type;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author davins
 */
public class RenavamTest
{

	public RenavamTest()
	{
	}

	@Test
	public void testValidate1()
	{
		Assert.assertTrue(Renavam.validate("0063988496-2"));
	}

	@Test
	public void testValidate2()
	{
		Assert.assertTrue(Renavam.validate("00639884962"));
	}

	@Test
	public void testValidate3()
	{
		Assert.assertTrue(Renavam.validate("63988496-2"));
	}

	@Test
	public void testValidate4()
	{
		Assert.assertTrue(Renavam.validate("639884962"));
	}

	@Test
	public void testValidate5()
	{
		Assert.assertTrue(Renavam.validate("1446676682-3"));
	}

	@Test
	public void testValidate6()
	{
		Assert.assertTrue(Renavam.validate("14466766823"));
	}

	@Test
	public void testFormat1()
	{
		Assert.assertEquals("0063988496-2", Renavam.format("0063988496-2"));
	}
	
	@Test
	public void testFormat2()
	{
		Assert.assertEquals("0063988496-2", Renavam.format("00639884962"));
	}
	
	@Test
	public void testFormat3()
	{
		Assert.assertEquals("0063988496-2", Renavam.format("63988496-2"));
	}
	
	@Test
	public void testFormat4()
	{
		Assert.assertEquals("0063988496-2", Renavam.format("639884962"));
	}
}
