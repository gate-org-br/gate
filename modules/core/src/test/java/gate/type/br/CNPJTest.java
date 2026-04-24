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
	}

	@Test
	public void validateStringReturnsFalseForInvalidCnpj()
	{
		assertFalse(CNPJ.validate("44.733.243/0001-98"));
		assertFalse(CNPJ.validate("44733243000198"));
		assertFalse(CNPJ.validate("44.733.243-0001/04"));
		assertFalse(CNPJ.validate(null));
	}

	@Test
	public void validateLongReturnsTrueOnlyForValidCnpjValue()
	{
		assertTrue(CNPJ.validate(69362335000156L));
		assertFalse(CNPJ.validate(69362335000157L));
		assertFalse(CNPJ.validate(-1L));
		assertFalse(CNPJ.validate(100_000_000_000_000L));
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
		String[] inputs = {
				"00",
				"000",
				"0000",
				"00000",
				"000000",
				"0000000",
				"00000000",
				"000000000",
				"0000000000",
				"00000000000",
				"000000000000",
				"0000000000000",
				"00000000000000",
				"00.000.000/0000-00"
		};

		for (String input : inputs)
			assertEquals("00.000.000/0000-00", CNPJ.format(input), input);
	}

	@Test
	public void formatLongReturnsFormattedCnpj()
	{
		assertEquals("69.362.335/0001-56", CNPJ.format(69362335000156L));
	}

	@Test
	public void formatLongReturnsNullForInvalidCnpj()
	{
		assertNull(CNPJ.format(69362335000157L));
		assertNull(CNPJ.format(-1L));
	}

	@Test
	public void digitsReturnsZeroPaddedRawCnpj()
	{
		assertEquals("69362335000156", CNPJ.digits(69362335000156L));
		assertEquals("00000000000000", CNPJ.digits(0L));
	}

	@Test
	public void digitsReturnsNullForInvalidCnpj()
	{
		assertNull(CNPJ.digits(69362335000157L));
		assertNull(CNPJ.digits(-1L));
	}

	@Test
	public void toLongParsesRawAndFormattedCnpj()
	{
		assertEquals(69362335000156L, CNPJ.toLong("69362335000156"));
		assertEquals(69362335000156L, CNPJ.toLong("69.362.335/0001-56"));
		assertEquals(0L, CNPJ.toLong("00.000.000/0000-00"));
	}

	@Test
	public void toLongReturnsMinusOneForInvalidInput()
	{
		assertEquals(-1L, CNPJ.toLong(null));
		assertEquals(-1L, CNPJ.toLong("44.733.243-0001/04"));
		assertEquals(-1L, CNPJ.toLong("44733243000198"));
		assertEquals(-1L, CNPJ.toLong("abc"));
	}

	@Test
	public void ofStringCreatesCnpjForValidInput()
	{
		assertEquals(new CNPJ(69362335000156L), CNPJ.of("69.362.335/0001-56"));
		assertEquals(new CNPJ(69362335000156L), CNPJ.of("69362335000156"));
	}

	@Test
	public void ofStringThrowsForInvalidInput()
	{
		assertThrows(IllegalArgumentException.class, () -> CNPJ.of("44733243000198"));
		assertThrows(IllegalArgumentException.class, () -> CNPJ.of((String) null));
	}

	@Test
	public void ofLongAndConstructorRejectInvalidValues()
	{
		assertThrows(IllegalArgumentException.class, () -> CNPJ.of(69362335000157L));
		assertThrows(IllegalArgumentException.class, () -> new CNPJ(69362335000157L));
	}

	@Test
	public void toStringReturnsFormattedCnpj()
	{
		assertEquals("69.362.335/0001-56", new CNPJ(69362335000156L).toString());
	}

	@Test
	public void compareToOrdersByNumericValue()
	{
		CNPJ smaller = new CNPJ(44733243000104L);
		CNPJ larger = new CNPJ(69362335000156L);

		assertTrue(smaller.compareTo(larger) < 0);
		assertTrue(larger.compareTo(smaller) > 0);
		assertEquals(0, smaller.compareTo(new CNPJ(44733243000104L)));
	}

	@Test
	public void equalityUsesCnpjValue()
	{
		assertEquals(new CNPJ(69362335000156L), new CNPJ(69362335000156L));
		assertNotEquals(new CNPJ(69362335000156L), new CNPJ(44733243000104L));
	}
}