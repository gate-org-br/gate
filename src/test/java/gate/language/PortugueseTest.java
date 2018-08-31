package gate.language;

import org.junit.Assert;
import org.junit.Test;

public class PortugueseTest
{

	@Test
	public void testCapitalizeWord()
	{
		Assert.assertEquals("Fulano", Language.PORTUGUESE.capitalizeWord("fulano"));
		Assert.assertEquals("Fulano", Language.PORTUGUESE.capitalizeWord("FULANO"));
		Assert.assertEquals("Fulano", Language.PORTUGUESE.capitalizeWord("FuLaNo"));
	}

	@Test
	public void testCapitalizeSentence()
	{
		Assert.assertEquals("Fulano da Silva dos Santos Souza",
				Language.PORTUGUESE.capitalizeName("fulano da silva dos santos souza"));
		Assert.assertEquals("Fulano da Silva dos Santos Souza",
				Language.PORTUGUESE.capitalizeName("FULANO DA SILVA DOS SANTOS SOUZA"));
		Assert.assertEquals("Fulano da Silva dos Santos Souza",
				Language.PORTUGUESE.capitalizeName("fulano DA silva DOS santos SOUZA"));
	}
}
