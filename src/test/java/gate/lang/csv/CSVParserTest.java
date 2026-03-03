package gate.lang.csv;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVParserTest
{

	@Test
	public void testUsers1()
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("/gate/lang/csv/CSVParserTest/Users1.csv")))
		{

			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			assertEquals(7, lines.size());

			int index = 0;
			assertEquals(List.of("User 1", "Role 1", "", "", "", "", "Name 1"), lines.get(index++));
			assertEquals(List.of("User 2", "Role 2", "", "", "", "", "Name 2"), lines.get(index++));
			assertTrue(lines.get(index++).isEmpty());
			assertEquals(List.of("User 3", "Role 3", "", "", "", "", "Name 3"), lines.get(index++));
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertEquals(List.of("User 4", "Role 4", "", "", "", "", "Name 4"), lines.get(index));
		}
	}

	@Test
	public void testUsers2()
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("/gate/lang/csv/CSVParserTest/Users2.csv")))
		{

			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			assertEquals(17, lines.size());

			int index = 0;
			assertEquals(List.of("User 1", "Role 1", "", "", "", "", "Name 1"), lines.get(index++));
			assertEquals(List.of("User 2", "Role 2", "", "", "", "", "Name 2"), lines.get(index++));
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertEquals(List.of("User 3", "Role 3", "", "", "", "", "Name 3"), lines.get(index++));
			assertEquals(List.of("User 4", "Role 4", "", "", "", "", "Name 4"), lines.get(index++));
			assertEquals(List.of("User 5", "Role 5", "", "", "", "", "Name 5"), lines.get(index));
		}
	}

	@Test
	public void testUsers3()
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("/gate/lang/csv/CSVParserTest/Users3.csv")))
		{

			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			assertEquals(19, lines.size());

			int index = 0;
			assertEquals(List.of("User 1", "Role 1", "", "", "", "", "Name 1"), lines.get(index++));
			assertEquals(List.of("User 2", "Role 2", "", "", "", "", "Name 2"), lines.get(index++));
			assertEquals(List.of("User 3", "Role 3", "", "", "", "", "Name 3"), lines.get(index++));
			assertEquals(List.of("User 4", "Role 4", "", "", "", "", "Name 4"), lines.get(index++));
			assertEquals(List.of("User 5", "Role 5", "", "", "", "", "Name 5"), lines.get(index++));
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index++).isEmpty());
			assertTrue(lines.get(index).isEmpty());
		}
	}

	@Test
	public void testUsers4()
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("/gate/lang/csv/CSVParserTest/Users4.csv"), ',', '\''))
		{
			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			assertEquals(5, lines.size());

			int index = 0;
			assertEquals(List.of("User 1", "Role 1", ""), lines.get(index++));
			assertEquals(List.of("User 2", "Role 2", ""), lines.get(index++));
			assertEquals(List.of("User 3", "Role 3", ""), lines.get(index++));
			assertEquals(List.of("User 4", "Role 4", ""), lines.get(index++));
			assertEquals(List.of("User 5", "Role 5", ""), lines.get(index));
		}
	}

	@Test
	public void testUsers5()
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("/gate/lang/csv/CSVParserTest/Users5.csv"), ';', '"'))
		{
			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			assertEquals(2, lines.size());

			int index = 0;
			assertEquals(List.of("User 4", "Role 4", "", "", "", "", "Name"), lines.get(index++));
			assertEquals(List.of("4\""), lines.get(index));
		}
	}

	@Test
	public void testCsv()
	{
		try (CSVParser parser = CSVParser.of(getClass().getResource("/gate/lang/csv/CSVParserTest/csv.csv")))
		{
			List<List<String>> lines = parser.stream().collect(Collectors.toList());
			assertEquals(3, lines.size());

			int index = 0;
			assertEquals(List.of("John Matheus", "Davis", "true"), lines.get(index++));
			assertEquals(List.of("Paul", "Richard\"", "false"), lines.get(index++));
			assertEquals(List.of("Marie", "Anderson", "true"), lines.get(index));
		}
	}
}
