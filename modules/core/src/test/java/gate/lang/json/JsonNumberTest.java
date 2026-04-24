package gate.lang.json;

import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class JsonNumberTest
{

	@Test
	public void shouldParseRegularAndScientificNumbers()
	{
		assertEquals(JsonNumber.of(30.0), JsonNumber.parse(JsonNumber.of(30.0).toString()));
		assertEquals(JsonNumber.of(310), JsonNumber.parse("3.1e2"));
		assertEquals(JsonNumber.of(300), JsonNumber.parse("3e2"));
		assertEquals(JsonNumber.of(300), JsonNumber.parse("3e+2"));
		assertEquals(JsonNumber.of(0.03), JsonNumber.parse("3e-2"));
		assertEquals(JsonNumber.of("-42.5"), JsonNumber.parse("-42.5"));
	}

	@Test
	public void shouldRenderNumbersAndNumericStrings()
	{
		assertEquals(JsonNumber.of(30), JsonNumber.render(30));
		assertEquals(JsonNumber.of(30.5), JsonNumber.render(30.5));
		assertEquals(JsonNumber.of("42"), JsonNumber.render("42"));
	}

	@Test
	public void shouldDecodeToPrimitiveWrapperAndBigTypes()
	{
		JsonNumber number = JsonNumber.of(42);

		assertEquals((byte) 42, number.decode(byte.class));
		assertEquals((byte) 42, number.decode(Byte.class));
		assertEquals((short) 42, number.decode(short.class));
		assertEquals((short) 42, number.decode(Short.class));
		assertEquals(42, number.decode(int.class));
		assertEquals(42, number.decode(Integer.class));
		assertEquals(42L, number.decode(long.class));
		assertEquals(42L, number.decode(Long.class));
		assertEquals(42.0f, number.decode(float.class));
		assertEquals(42.0f, number.decode(Float.class));
		assertEquals(42.0d, number.decode(double.class));
		assertEquals(42.0d, number.decode(Double.class));
		assertEquals(BigInteger.valueOf(42), number.decode(BigInteger.class));
		assertEquals(BigDecimal.valueOf(42), number.decode(BigDecimal.class));
		assertEquals(Integer.valueOf(42), number.decode(Integer.class, null));
	}

	@Test
	public void shouldExposeNumberValuesAndNaturalRepresentation()
	{
		JsonNumber number = JsonNumber.of("42.5");

		assertEquals(JsonElement.Type.NUMBER, number.getType());
		assertEquals(42, number.intValue());
		assertEquals(42L, number.longValue());
		assertEquals((short) 42, number.shortValue());
		assertEquals((byte) 42, number.byteValue());
		assertEquals(42.5f, number.floatValue());
		assertEquals(42.5d, number.doubleValue());
		assertEquals(new BigDecimal("42.5"), number.getValue());
		assertEquals(new BigDecimal("42.5"), number.getScalarValue());
		assertEquals(new BigDecimal("42.5"), number.unwrap());
		assertEquals("42.5", number.toString());
	}

	@Test
	public void shouldRejectNonNumberJson()
	{
		assertThrows(ConversionException.class, () -> JsonNumber.parse("\"42\""));
		assertThrows(NullPointerException.class, () -> JsonNumber.parse(null));
	}
}
