package gate.type;

import org.junit.Assert;
import org.junit.Test;

public class PortugueseNameTest
{

	@Test
	public void test1()
	{
		Assert.assertEquals("Fulano da Silva Souza",
			PortugueseName.valueOf("Fulano da Silva Souza").toString());
	}

	@Test
	public void test2()
	{
		Assert.assertEquals("Scriptscript",
			PortugueseName.valueOf("<script></script>").toString());
	}

	@Test
	public void test3()
	{
		Assert.assertEquals("Fátima Bernardes Costa e Silva",
			PortugueseName.valueOf("Fátima Bernardes Costa e Silva").toString());
	}
}
