package gate.lang.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class CSVParserTest
{

	@Test
	public void testUsers1() throws IOException
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("CSVParserTest/Users1.csv")))
		{

			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			Assert.assertEquals(7, lines.size());

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
	public void testUsers2() throws IOException
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("CSVParserTest/Users2.csv")))
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
	public void testUsers3() throws IOException
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("CSVParserTest/Users3.csv")))
		{

			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			Assert.assertEquals(19, lines.size());

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
		}
	}

	@Test
	public void testUsers4() throws IOException
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("CSVParserTest/Users4.csv"), ',', '\''))
		{
			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			Assert.assertEquals(5, lines.size());

			int index = 0;
			Assert.assertEquals(List.of("User 1", "Role 1", ""), lines.get(index++));
			Assert.assertEquals(List.of("User 2", "Role 2", ""), lines.get(index++));
			Assert.assertEquals(List.of("User 3", "Role 3", ""), lines.get(index++));
			Assert.assertEquals(List.of("User 4", "Role 4", ""), lines.get(index++));
			Assert.assertEquals(List.of("User 5", "Role 5", ""), lines.get(index++));
		}
	}

	@Test
	public void testUsers5() throws IOException
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("CSVParserTest/Users5.csv"), ';', '"'))
		{
			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			Assert.assertEquals(2, lines.size());

			int index = 0;
			Assert.assertEquals(List.of("User 4", "Role 4", "", "", "", "", "Name"), lines.get(index++));
			Assert.assertEquals(List.of("4\""), lines.get(index++));
		}
	}

	@Test
	public void testCsv() throws IOException
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("CSVParserTest/csv.csv")))
		{
			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			Assert.assertEquals(3, lines.size());

			int index = 0;
			Assert.assertEquals(List.of("Jonh Matheus", "Davis", "true"), lines.get(index++));
			Assert.assertEquals(List.of("Paul", "Richard\"", "false"), lines.get(index++));
			Assert.assertEquals(List.of("Marie", "Anderson", "true"), lines.get(index++));
		}
	}

	@Test
	public void testCSVIterator() throws IOException
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("CSVParserTest/csv.csv")))
		{
			List<List<String>> lines
				= new ArrayList<>();
			for (List<String> line : parser)
				lines.add(line);

			Assert.assertEquals(3, lines.size());

			int index = 0;
			Assert.assertEquals(List.of("Jonh Matheus", "Davis", "true"), lines.get(index++));
			Assert.assertEquals(List.of("Paul", "Richard\"", "false"), lines.get(index++));
			Assert.assertEquals(List.of("Marie", "Anderson", "true"), lines.get(index++));
		}
	}

}
