package gate.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SafeStyleTest
{
	@Test
	public void shouldKeepAllowedProperties()
	{
		assertEquals("color: red; font-weight: bold; margin: 10px 5px",
				SafeStyle.of("color:red; font-weight:bold; margin:10px 5px").toString());
	}

	@Test
	public void shouldDropForbiddenProperties()
	{
		assertEquals("color: red",
				SafeStyle.of("position: fixed; top: 0; color: red; z-index: 9999").toString());
	}

	@Test
	public void shouldDropDangerousValues()
	{
		assertTrue(SafeStyle.of("color: expression(alert(1)); background-color: url(javascript:alert(1))").isBlank());
	}

	@Test
	public void shouldKeepHyphenatedProperties()
	{
		assertEquals("background-color: #fff; text-align: center; border-bottom: 1px solid #000",
				SafeStyle.of("background-color:#fff; text-align:center; border-bottom:1px solid #000").toString());
	}
}
