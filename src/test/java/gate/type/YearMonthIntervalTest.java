package gate.type;

import java.text.ParseException;
import org.junit.Assert;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.Test;

public class YearMonthIntervalTest
{

	private final YearMonthInterval OBJECT = YearMonthInterval.of(YearMonth.of(2018, java.time.Month.NOVEMBER), YearMonth.of(2019, java.time.Month.FEBRUARY));

	@Test
	public void testContainsYearMonth() throws ParseException
	{
		Assert.assertEquals(true, OBJECT.contains(YearMonth.of(2018, java.time.Month.NOVEMBER)));
		Assert.assertEquals(false, OBJECT.contains(YearMonth.of(2019, java.time.Month.NOVEMBER)));
	}

	@Test
	public void testContainsYearMonthInterval() throws ParseException
	{
		Assert.assertEquals(true, OBJECT.contains(YearMonthInterval.of(YearMonth.of(2018, java.time.Month.NOVEMBER),
			YearMonth.of(2019, java.time.Month.JANUARY))));

		Assert.assertEquals(false, OBJECT.contains(YearMonthInterval.of(YearMonth.of(2018, java.time.Month.NOVEMBER),
			YearMonth.of(2019, java.time.Month.MARCH))));
	}

	@Test
	public void testStream() throws ParseException
	{
		Assert.assertEquals(Arrays.asList(YearMonth.of(2018, java.time.Month.NOVEMBER),
			YearMonth.of(2018, java.time.Month.DECEMBER),
			YearMonth.of(2019, java.time.Month.JANUARY),
			YearMonth.of(2019, java.time.Month.FEBRUARY)),
			OBJECT.stream().collect(Collectors.toList()));
	}
}
