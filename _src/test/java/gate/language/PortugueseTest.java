package gate.language;

import org.junit.Assert;
import org.junit.Test;

public class PortugueseTest
{

	@Test
	public void testCapitalizeSentence()
	{
		Assert.assertEquals("Fulano da Silva dos Santos Souza",
				Language.PORTUGUESE.capitalize("fulano da silva dos santos souza"));
		Assert.assertEquals("Fulano da Silva dos Santos Souza",
				Language.PORTUGUESE.capitalize("FULANO DA SILVA DOS SANTOS SOUZA"));
		Assert.assertEquals("Fulano da Silva dos Santos Souza",
				Language.PORTUGUESE.capitalize("fulano DA silva DOS santos SOUZA"));
	}
}
