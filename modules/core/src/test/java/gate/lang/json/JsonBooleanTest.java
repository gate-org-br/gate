package gate.lang.json;

import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonBooleanTest
{

	@Test
	public void shouldParseRenderAndPreserveSingletons()
	{
		assertSame(JsonBoolean.TRUE, JsonBoolean.parse("true"));
		assertSame(JsonBoolean.FALSE, JsonBoolean.parse("false"));
		assertSame(JsonBoolean.TRUE, JsonBoolean.of(true));
		assertSame(JsonBoolean.FALSE, JsonBoolean.of(Boolean.FALSE));
		assertSame(JsonBoolean.TRUE, JsonBoolean.render(Boolean.TRUE));
		assertSame(JsonBoolean.FALSE, JsonElement.render(Boolean.FALSE));
	}

	@Test
	public void shouldDecodeAndUnwrapBooleans()
	{
		assertEquals(JsonElement.Type.BOOLEAN, JsonBoolean.TRUE.getType());
		assertTrue(JsonBoolean.TRUE.getValue());
		assertEquals(true, JsonBoolean.TRUE.getScalarValue());
		assertEquals(true, JsonBoolean.TRUE.unwrap());
		assertEquals(true, JsonBoolean.TRUE.decode(Boolean.class));
		assertEquals(true, JsonBoolean.TRUE.decode(boolean.class));
		assertEquals(true, JsonBoolean.TRUE.decode(Boolean.class, null));
		assertEquals("true", JsonBoolean.TRUE.toString());
		assertEquals(1, JsonBoolean.TRUE.hashCode());
		assertEquals(0, JsonBoolean.FALSE.hashCode());
	}

	@Test
	public void shouldRejectNonBooleanJson()
	{
		assertThrows(ConversionException.class, () -> JsonBoolean.parse("\"true\""));
		assertThrows(NullPointerException.class, () -> JsonBoolean.parse(null));
	}
}