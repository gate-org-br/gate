package gate.type;

import gate.language.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SafeNameTest
{
	@Test
	public void shouldPreserveValidSafeName()
	{
		assertEquals("Fulano da Silva Souza", SafeName.valueOf("Fulano da Silva Souza").toString());
	}

	@Test
	public void shouldCollapseExtraSpaces()
	{
		assertEquals("Fulano da Silva", SafeName.valueOf("  Fulano   da   Silva  ").toString());
	}

	@Test
	public void shouldRejectDangerousCharacters()
	{
		assertThrows(IllegalArgumentException.class, () -> SafeName.valueOf("<script></script>"));
		assertThrows(IllegalArgumentException.class, () -> SafeName.valueOf("D'Avila"));
		assertThrows(IllegalArgumentException.class, () -> SafeName.valueOf("ACME, Ltda"));
	}

	@Test
	public void shouldFormatPortugueseNames()
	{
		assertEquals("Fatima Bernardes Costa e Silva",
				SafeName.valueOf("fatima bernardes costa e silva")
						.format(Language.PORTUGUESE)
						.toString());
	}

	@Test
	public void shouldFormatEnglishNames()
	{
		assertEquals("The Lord of the Rings",
				SafeName.valueOf("the lord of the rings")
						.format(Language.ENGLISH)
						.toString());
		assertEquals("Mary-Jane Watson",
				SafeName.valueOf("mary-jane watson")
						.format(Language.ENGLISH)
						.toString());
	}
}
