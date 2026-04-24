package gate.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SafeTextTest
{
	@Test
	public void shouldPreservePlainText()
	{
		assertEquals("Linha 1.\nLinha 2: ok!", SafeText.valueOf("Linha 1.\nLinha 2: ok!").toString());
	}

	@Test
	public void shouldNormalizeLineEndings()
	{
		assertEquals("Linha 1\nLinha 2", SafeText.valueOf("Linha 1\r\nLinha 2").toString());
	}

	@Test
	public void shouldRejectDangerousCharacters()
	{
		assertThrows(IllegalArgumentException.class, () -> SafeText.valueOf("<script>alert(1)</script>"));
		assertThrows(IllegalArgumentException.class, () -> SafeText.valueOf("texto \"quebrado\""));
		assertThrows(IllegalArgumentException.class, () -> SafeText.valueOf("valor = 1"));
	}

	@Test
	public void shouldAllowBlankCheck()
	{
		assertTrue(SafeText.valueOf("   ").isBlank());
	}
}
