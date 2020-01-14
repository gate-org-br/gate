package gate.lang.csv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class CSVParserTest
{

	@Test
	public void testUsers1()
	{
		try (CSVParser parser = new CSVParser(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("Users1.csv")))))
		{

			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			Assert.assertEquals(8, lines.size());

			int index = 0;
			Assert.assertEquals(List.of("User 1", "Role 1", "", "", "", "", "Name 1"), lines.get(index++));
			Assert.assertEquals(List.of("User 2", "Role 2", "", "", "", "", "Name 2"), lines.get(index++));
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertEquals(List.of("User 3", "Role 3", "", "", "", "", "Name 3"), lines.get(index++));
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertEquals(List.of("User 4", "Role 4", "", "", "", "", "Name 4"), lines.get(index++));
		}
	}

	@Test
	public void testUsers2()
	{
		try (CSVParser parser = new CSVParser(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("Users2.csv")))))
		{

			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			Assert.assertEquals(17, lines.size());

			int index = 0;
			Assert.assertEquals(List.of("User 1", "Role 1", "", "", "", "", "Name 1"), lines.get(index++));
			Assert.assertEquals(List.of("User 2", "Role 2", "", "", "", "", "Name 2"), lines.get(index++));
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertEquals(List.of("User 3", "Role 3", "", "", "", "", "Name 3"), lines.get(index++));
			Assert.assertEquals(List.of("User 4", "Role 4", "", "", "", "", "Name 4"), lines.get(index++));
			Assert.assertEquals(List.of("User 5", "Role 5", "", "", "", "", "Name 5"), lines.get(index++));
		}
	}

	@Test
	public void testUsers3()
	{
		try (CSVParser parser = new CSVParser(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("Users3.csv")))))
		{

			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			Assert.assertEquals(20, lines.size());

			int index = 0;
			Assert.assertEquals(List.of("User 1", "Role 1", "", "", "", "", "Name 1"), lines.get(index++));
			Assert.assertEquals(List.of("User 2", "Role 2", "", "", "", "", "Name 2"), lines.get(index++));
			Assert.assertEquals(List.of("User 3", "Role 3", "", "", "", "", "Name 3"), lines.get(index++));
			Assert.assertEquals(List.of("User 4", "Role 4", "", "", "", "", "Name 4"), lines.get(index++));
			Assert.assertEquals(List.of("User 5", "Role 5", "", "", "", "", "Name 5"), lines.get(index++));
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
			Assert.assertTrue(lines.get(index++).isEmpty());
		}
	}

	@Test
	public void testUsers4()
	{
		try (CSVParser parser = new CSVParser(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("Users4.csv"))), ',', '\''))
		{
			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			Assert.assertEquals(6, lines.size());

			int index = 0;
			Assert.assertEquals(List.of("User 1", "Role 1", ""), lines.get(index++));
			Assert.assertEquals(List.of("User 2", "Role 2", ""), lines.get(index++));
			Assert.assertEquals(List.of("User 3", "Role 3", ""), lines.get(index++));
			Assert.assertEquals(List.of("User 4", "Role 4", ""), lines.get(index++));
			Assert.assertEquals(List.of("User 5", "Role 5", ""), lines.get(index++));
			Assert.assertTrue(lines.get(index++).isEmpty());
		}
	}

	@Test
	public void testCsv()
	{
		try (CSVParser parser = new CSVParser(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("csv.csv")))))
		{
			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			Assert.assertEquals(3, lines.size());

			int index = 0;
			Assert.assertEquals(List.of("Jonh Matheus", "Davis", "true"), lines.get(index++));
			Assert.assertEquals(List.of("Paul", "Richard\"", "false"), lines.get(index++));
			Assert.assertEquals(List.of("Marie", "Anderson", "true"), lines.get(index++));
		}
	}

}