package gate.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class PortugueseNameTest
{

	@Test
	public void test1()
	{
		assertEquals("Fulano da Silva Souza",
			PortugueseName.valueOf("Fulano da Silva Souza").toString());
	}

	@Test
	public void test2()
	{
		assertEquals("Scriptscript",
			PortugueseName.valueOf("<script></script>").toString());
	}

	@Test
	public void test3()
	{
		assertEquals("Fátima Bernardes Costa e Silva",
			PortugueseName.valueOf("Fátima Bernardes Costa e Silva").toString());
	}
}
