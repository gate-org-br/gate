package gate.type.br;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CNPJTest
{
	@Test
	public void validateStringReturnsTrueForValidFormattedCnpj()
	{
		assertTrue(CNPJ.validate("69.362.335/0001-56"));
		assertTrue(CNPJ.validate("44.733.243/0001-04"));
		assertTrue(CNPJ.validate("00.000.000/0001-91"));
		assertTrue(CNPJ.validate("12.ABC.345/01DE-35"));
	}

	@Test
	public void validateStringReturnsTrueForValidRawCnpj()
	{
		assertTrue(CNPJ.validate("69362335000156"));
		assertTrue(CNPJ.validate("44733243000104"));
		assertTrue(CNPJ.validate("12ABC34501DE35"));
	}

	@Test
	public void validateStringReturnsFalseForInvalidCnpj()
	{
		assertFalse(CNPJ.validate("44.733.243/0001-98"));
		assertFalse(CNPJ.validate("44733243000198"));
		assertFalse(CNPJ.validate("12.ABC.345/01DE-36"));
		assertFalse(CNPJ.validate("12ABC34501DE36"));
		assertFalse(CNPJ.validate("44.733.243-0001/04"));
		assertFalse(CNPJ.validate(null));
	}

	@Test
	public void formatStringReturnsNullForInvalidInput()
	{
		assertNull(CNPJ.format("0"));
		assertNull(CNPJ.format("abc"));
		assertNull(CNPJ.format(null));
	}

	@Test
	public void formatStringNormalizesValidInputsWithOrWithoutPunctuation()
	{
		assertEquals("69.362.335/0001-56", CNPJ.format("69362335000156"));
		assertEquals("69.362.335/0001-56", CNPJ.format("69.362.335/0001-56"));
		assertEquals("12.ABC.345/01DE-35", CNPJ.format("12ABC34501DE35"));
		assertEquals("12.ABC.345/01DE-35", CNPJ.format("12.abc.345/01de-35"));
	}

	@Test
	public void rawReturnsUnformattedCnpj()
	{
		assertEquals("69362335000156", CNPJ.raw("69.362.335/0001-56"));
		assertEquals("69362335000156", CNPJ.raw("69362335000156"));
		assertEquals("12ABC34501DE35", CNPJ.raw("12.ABC.345/01DE-35"));
		assertEquals("12ABC34501DE35", CNPJ.raw("12abc34501de35"));
	}

	@Test
	public void rawReturnsNullForInvalidCnpj()
	{
		assertNull(CNPJ.raw("44.733.243/0001-98"));
		assertNull(CNPJ.raw("44733243000198"));
		assertNull(CNPJ.raw(null));
	}

	@Test
	public void valueOfStringCreatesCnpjForValidInput()
	{
		assertEquals(new CNPJ("69.362.335/0001-56"), CNPJ.valueOf("69362335000156"));
		assertEquals(new CNPJ("12.ABC.345/01DE-35"), CNPJ.valueOf("12abc34501de35"));
	}

	@Test
	public void valueOfStringThrowsForInvalidInput()
	{
		assertThrows(IllegalArgumentException.class, () -> CNPJ.valueOf("44733243000198"));
		assertThrows(IllegalArgumentException.class, () -> CNPJ.valueOf((String) null));
	}

	@Test
	public void toStringReturnsFormattedCnpj()
	{
		assertEquals("69.362.335/0001-56", new CNPJ("69362335000156").toString());
		assertEquals("12.ABC.345/01DE-35", new CNPJ("12abc34501de35").toString());
	}

	@Test
	public void compareToOrdersByFormattedValue()
	{
		CNPJ smaller = new CNPJ("44.733.243/0001-04");
		CNPJ larger = new CNPJ("69.362.335/0001-56");

		assertTrue(smaller.compareTo(larger) < 0);
		assertTrue(larger.compareTo(smaller) > 0);
		assertEquals(0, smaller.compareTo(new CNPJ("44733243000104")));
	}

	@Test
	public void equalityUsesFormattedCnpjValue()
	{
		assertEquals(new CNPJ("69.362.335/0001-56"), new CNPJ("69362335000156"));
		assertNotEquals(new CNPJ("69.362.335/0001-56"), new CNPJ("44.733.243/0001-04"));
	}
}
