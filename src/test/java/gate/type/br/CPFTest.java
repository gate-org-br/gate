package gate.type.br;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CPFTest
{
	@Test
	public void validateStringReturnsTrueForValidFormattedCpf()
	{
		assertTrue(CPF.validate("314.343.884-33"));
		assertTrue(CPF.validate("977.234.477-79"));
	}

	@Test
	public void validateStringReturnsFalseForInvalidCpf()
	{
		assertFalse(CPF.validate("000.000.000-01"));
		assertFalse(CPF.validate("31434388434"));
		assertFalse(CPF.validate("314.343.884/33"));
		assertFalse(CPF.validate(null));
	}

	@Test
	public void validateLongReturnsTrueOnlyForValidCpfValue()
	{
		assertTrue(CPF.validate(31434388433L));
		assertFalse(CPF.validate(31434388434L));
		assertFalse(CPF.validate(-1L));
		assertFalse(CPF.validate(100_000_000_000L));
	}

	@Test
	public void formatStringReturnsNullForInvalidInput()
	{
		assertNull(CPF.format("0"));
		assertNull(CPF.format("abc"));
		assertNull(CPF.format(null));
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
				"000.000.000-00"
		};

		for (String input : inputs)
			assertEquals("000.000.000-00", CPF.format(input), input);
	}

	@Test
	public void formatLongReturnsFormattedCpf()
	{
		assertEquals("314.343.884-33", CPF.format(31434388433L));
	}

	@Test
	public void formatLongReturnsNullForInvalidCpf()
	{
		assertNull(CPF.format(31434388434L));
		assertNull(CPF.format(-1L));
	}

	@Test
	public void digitsReturnsZeroPaddedRawCpf()
	{
		assertEquals("31434388433", CPF.digits(31434388433L));
		assertEquals("00000000000", CPF.digits(0L));
	}

	@Test
	public void digitsReturnsNullForInvalidCpf()
	{
		assertNull(CPF.digits(31434388434L));
		assertNull(CPF.digits(-1L));
	}

	@Test
	public void toLongParsesRawAndFormattedCpf()
	{
		assertEquals(31434388433L, CPF.toLong("31434388433"));
		assertEquals(31434388433L, CPF.toLong("314.343.884-33"));
		assertEquals(0L, CPF.toLong("000.000.000-00"));
	}

	@Test
	public void toLongReturnsMinusOneForInvalidInput()
	{
		assertEquals(-1L, CPF.toLong(null));
		assertEquals(-1L, CPF.toLong("314.343.884/33"));
		assertEquals(-1L, CPF.toLong("31434388434"));
		assertEquals(-1L, CPF.toLong("abc"));
	}

	@Test
	public void ofStringCreatesCpfForValidInput()
	{
		assertEquals(new CPF(31434388433L), CPF.of("314.343.884-33"));
		assertEquals(new CPF(31434388433L), CPF.of("31434388433"));
	}

	@Test
	public void ofStringThrowsForInvalidInput()
	{
		assertThrows(IllegalArgumentException.class, () -> CPF.of("31434388434"));
		assertThrows(IllegalArgumentException.class, () -> CPF.of((String) null));
	}

	@Test
	public void ofLongAndConstructorRejectInvalidValues()
	{
		assertThrows(IllegalArgumentException.class, () -> CPF.of(31434388434L));
		assertThrows(IllegalArgumentException.class, () -> new CPF(31434388434L));
	}

	@Test
	public void toStringReturnsFormattedCpf()
	{
		assertEquals("314.343.884-33", new CPF(31434388433L).toString());
	}

	@Test
	public void compareToOrdersByNumericValue()
	{
		CPF smaller = new CPF(31434388433L);
		CPF larger = new CPF(97723447779L);

		assertTrue(smaller.compareTo(larger) < 0);
		assertTrue(larger.compareTo(smaller) > 0);
		assertEquals(0, smaller.compareTo(new CPF(31434388433L)));
	}

	@Test
	public void equalityUsesCpfValue()
	{
		assertEquals(new CPF(31434388433L), new CPF(31434388433L));
		assertNotEquals(new CPF(31434388433L), new CPF(97723447779L));
	}
}