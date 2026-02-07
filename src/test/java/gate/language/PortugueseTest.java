package gate.language;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PortugueseTest
{

	@Test
	public void testCapitalizeSentence()
	{
		assertEquals("Fulano da Silva dos Santos Souza",
			Language.PORTUGUESE.capitalize("fulano da silva dos santos souza"));
		assertEquals("Fulano da Silva dos Santos Souza",
			Language.PORTUGUESE.capitalize("FULANO DA SILVA DOS SANTOS SOUZA"));
		assertEquals("Fulano da Silva dos Santos Souza",
			Language.PORTUGUESE.capitalize("fulano DA silva DOS santos SOUZA"));
	}
}
