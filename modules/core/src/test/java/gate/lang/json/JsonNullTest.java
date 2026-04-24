package gate.lang.json;

import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonNullTest
{

	@Test
	public void shouldParseRenderAndPreserveSingleton()
	{
		assertSame(JsonNull.INSTANCE, JsonNull.parse("null"));
		assertSame(JsonNull.INSTANCE, JsonNull.render());
		assertSame(JsonNull.INSTANCE, JsonNull.of());
		assertEquals(JsonElement.Type.NULL, JsonNull.INSTANCE.getType());
		assertEquals("null", JsonNull.INSTANCE.toString());
		assertEquals(0, JsonNull.INSTANCE.hashCode());
		assertEquals(JsonNull.INSTANCE, JsonNull.of());
		assertNotEquals(JsonNull.INSTANCE, new Object());
	}

	@Test
	public void shouldDecodeAndUnwrapToNull()
	{
		assertNull(JsonNull.INSTANCE.decode(String.class));
		assertNull(JsonNull.INSTANCE.decode(String.class, null));
		assertNull(JsonNull.INSTANCE.getScalarValue());
		assertNull(JsonNull.INSTANCE.unwrap());
	}

	@Test
	public void shouldRejectNonNullJson()
	{
		assertThrows(ConversionException.class, () -> JsonNull.parse("false"));
		assertThrows(NullPointerException.class, () -> JsonNull.parse(null));
	}
}
