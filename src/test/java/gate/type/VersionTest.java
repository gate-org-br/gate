package gate.type;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class VersionTest
{

	@Test
	public void test1() throws ParseException
	{
		String expected = "1.2.3";
		Version version = Version.of(expected);
		Assert.assertEquals(expected, version.toString());
	}

	@Test
	public void test2() throws ParseException
	{
		String expected = "1.2.3-SNAPSHOT";
		Version version = Version.of(expected);
		Assert.assertEquals(expected, version.toString());
	}

	@Test
	public void test3() throws ParseException
	{
		String expected = "1.2.3-RC-01";
		Version version = Version.of(expected);
		Assert.assertEquals(expected, version.toString());
	}

	@Test
	public void test4() throws ParseException
	{
		Version version = Version.of("1.2.3-RC-02");
		Assert.assertEquals(1, version.getMajor());
		Assert.assertEquals(2, version.getMinor());
		Assert.assertEquals(3, version.getPatch());
		Assert.assertEquals("RC", version.getQualifier());
		Assert.assertEquals("02", version.getIteration());
	}

	@Test
	public void test5() throws ParseException
	{
		List<Version> versions = new ArrayList<>();
		versions.add(Version.of("1.2.3"));
		versions.add(Version.of("1.2.3-SNAPSHOT"));
		versions.add(Version.of("1.2.3-RC-01"));
		versions.add(Version.of("2.2.3-SNAPSHOT"));
		versions.add(Version.of("3.2.3-RC-01"));
		versions.add(Version.of("3.2.3"));
		Collections.sort(versions);

		Assert.assertEquals("3.2.3", versions.get(0).toString());
		Assert.assertEquals("3.2.3-RC-01", versions.get(1).toString());
		Assert.assertEquals("2.2.3-SNAPSHOT", versions.get(2).toString());
		Assert.assertEquals("1.2.3", versions.get(3).toString());
		Assert.assertEquals("1.2.3-SNAPSHOT", versions.get(4).toString());
		Assert.assertEquals("1.2.3-RC-01", versions.get(5).toString());

	}
}
