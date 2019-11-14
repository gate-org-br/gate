package gate.lang.csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class CSVParserTest
{

	@Test
	public void testParseLine() throws IOException
	{
		try (CSVParser reader = new CSVParser(new InputStreamReader(getClass().getResourceAsStream("csv.csv"))))
		{
			List<List<String>> lines = new ArrayList<>();
			for (Optional<List<String>> line = reader.parseLine();
				line.isPresent(); line = reader.parseLine())
				lines.add(line.get());

			Assert.assertEquals(3, lines.size());

			Assert.assertEquals(3, lines.get(0).size());
			Assert.assertEquals("Jonh Matheus", lines.get(0).get(0));
			Assert.assertEquals("Davis", lines.get(0).get(1));
			Assert.assertEquals("true", lines.get(0).get(2));

			Assert.assertEquals(3, lines.get(1).size());
			Assert.assertEquals("Paul", lines.get(1).get(0));
			Assert.assertEquals("Richard\"", lines.get(1).get(1));
			Assert.assertEquals("false", lines.get(1).get(2));

			Assert.assertEquals(3, lines.get(2).size());
			Assert.assertEquals("Marie", lines.get(2).get(0));
			Assert.assertEquals("Anderson", lines.get(2).get(1));
			Assert.assertEquals("true", lines.get(2).get(2));

		}
	}

	@Test
	public void testIterator() throws IOException
	{
		try (CSVParser reader = new CSVParser(new InputStreamReader(getClass().getResourceAsStream("csv.csv"))))
		{
			List<List<String>> lines = new ArrayList<>();
			for (List<String> line : reader)
				lines.add(line);

			Assert.assertEquals(3, lines.size());

			Assert.assertEquals(3, lines.get(0).size());
			Assert.assertEquals("Jonh Matheus", lines.get(0).get(0));
			Assert.assertEquals("Davis", lines.get(0).get(1));
			Assert.assertEquals("true", lines.get(0).get(2));

			Assert.assertEquals(3, lines.get(1).size());
			Assert.assertEquals("Paul", lines.get(1).get(0));
			Assert.assertEquals("Richard\"", lines.get(1).get(1));
			Assert.assertEquals("false", lines.get(1).get(2));

			Assert.assertEquals(3, lines.get(2).size());
			Assert.assertEquals("Marie", lines.get(2).get(0));
			Assert.assertEquals("Anderson", lines.get(2).get(1));
			Assert.assertEquals("true", lines.get(2).get(2));

		}
	}

	@Test
	public void testStream() throws IOException
	{
		try (CSVParser reader = new CSVParser(new InputStreamReader(getClass().getResourceAsStream("csv.csv"))))
		{
			List<List<String>> lines = reader.stream().collect(Collectors.toList());

			Assert.assertEquals(3, lines.size());

			Assert.assertEquals(3, lines.get(0).size());
			Assert.assertEquals("Jonh Matheus", lines.get(0).get(0));
			Assert.assertEquals("Davis", lines.get(0).get(1));
			Assert.assertEquals("true", lines.get(0).get(2));

			Assert.assertEquals(3, lines.get(1).size());
			Assert.assertEquals("Paul", lines.get(1).get(0));
			Assert.assertEquals("Richard\"", lines.get(1).get(1));
			Assert.assertEquals("false", lines.get(1).get(2));

			Assert.assertEquals(3, lines.get(2).size());
			Assert.assertEquals("Marie", lines.get(2).get(0));
			Assert.assertEquals("Anderson", lines.get(2).get(1));
			Assert.assertEquals("true", lines.get(2).get(2));

		}
	}

	@Test
	public void testParseUsers1() throws IOException
	{
		try (CSVParser reader = new CSVParser(new InputStreamReader(getClass().getResourceAsStream("Users1.csv"))))
		{
			List<List<String>> lines = new ArrayList<>();
			for (Optional<List<String>> line = reader.parseLine();
				line.isPresent(); line = reader.parseLine())
				lines.add(line.get());

			Assert.assertEquals(13, lines.size());

			Assert.assertEquals(7, lines.get(0).size());
			Assert.assertEquals("User 1", lines.get(0).get(0));
			Assert.assertEquals("Role 1", lines.get(0).get(1));
			Assert.assertEquals("", lines.get(0).get(2));
			Assert.assertEquals("", lines.get(0).get(3));
			Assert.assertEquals("", lines.get(0).get(4));
			Assert.assertEquals("", lines.get(0).get(5));
			Assert.assertEquals("Name 1", lines.get(0).get(6));

			Assert.assertEquals(7, lines.get(1).size());
			Assert.assertEquals("User 2", lines.get(1).get(0));
			Assert.assertEquals("Role 2", lines.get(1).get(1));
			Assert.assertEquals("", lines.get(1).get(2));
			Assert.assertEquals("", lines.get(1).get(3));
			Assert.assertEquals("", lines.get(1).get(4));
			Assert.assertEquals("", lines.get(1).get(5));
			Assert.assertEquals("Name 2", lines.get(1).get(6));

			Assert.assertEquals(7, lines.get(2).size());
			Assert.assertEquals("User 3", lines.get(2).get(0));
			Assert.assertEquals("Role 3", lines.get(2).get(1));
			Assert.assertEquals("", lines.get(2).get(2));
			Assert.assertEquals("", lines.get(2).get(3));
			Assert.assertEquals("", lines.get(2).get(4));
			Assert.assertEquals("", lines.get(2).get(5));
			Assert.assertEquals("Name 3", lines.get(2).get(6));

			Assert.assertTrue(lines.get(3).isEmpty());
			Assert.assertTrue(lines.get(4).isEmpty());
			Assert.assertTrue(lines.get(5).isEmpty());
			Assert.assertTrue(lines.get(6).isEmpty());
			Assert.assertTrue(lines.get(7).isEmpty());
			Assert.assertTrue(lines.get(8).isEmpty());
			Assert.assertTrue(lines.get(9).isEmpty());

			Assert.assertEquals(7, lines.get(10).size());
			Assert.assertEquals("User 4", lines.get(10).get(0));
			Assert.assertEquals("Role 4", lines.get(10).get(1));
			Assert.assertEquals("", lines.get(10).get(2));
			Assert.assertEquals("", lines.get(10).get(3));
			Assert.assertEquals("", lines.get(10).get(4));
			Assert.assertEquals("", lines.get(10).get(5));
			Assert.assertEquals("Name 4", lines.get(10).get(6));

			Assert.assertEquals(7, lines.get(11).size());
			Assert.assertEquals("User 5", lines.get(11).get(0));
			Assert.assertEquals("Role 5", lines.get(11).get(1));
			Assert.assertEquals("", lines.get(11).get(2));
			Assert.assertEquals("", lines.get(11).get(3));
			Assert.assertEquals("", lines.get(11).get(4));
			Assert.assertEquals("", lines.get(11).get(5));
			Assert.assertEquals("Name 5", lines.get(11).get(6));

			Assert.assertTrue(lines.get(12).isEmpty());
		}
	}

	@Test
	public void testParseUsers2() throws IOException
	{
		try (CSVParser reader = new CSVParser(new InputStreamReader(getClass().getResourceAsStream("Users2.csv"))))
		{
			List<List<String>> lines = new ArrayList<>();
			for (Optional<List<String>> line = reader.parseLine();
				line.isPresent(); line = reader.parseLine())
				lines.add(line.get());

			Assert.assertEquals(17, lines.size());

			Assert.assertEquals(7, lines.get(0).size());
			Assert.assertEquals("User 1", lines.get(0).get(0));
			Assert.assertEquals("Role 1", lines.get(0).get(1));
			Assert.assertEquals("", lines.get(0).get(2));
			Assert.assertEquals("", lines.get(0).get(3));
			Assert.assertEquals("", lines.get(0).get(4));
			Assert.assertEquals("", lines.get(0).get(5));
			Assert.assertEquals("Name 1", lines.get(0).get(6));

			Assert.assertEquals("User 2", lines.get(1).get(0));
			Assert.assertEquals("Role 2", lines.get(1).get(1));
			Assert.assertEquals("", lines.get(1).get(2));
			Assert.assertEquals("", lines.get(1).get(3));
			Assert.assertEquals("", lines.get(1).get(4));
			Assert.assertEquals("", lines.get(1).get(5));
			Assert.assertEquals("Name 2", lines.get(1).get(6));

			Assert.assertTrue(lines.get(3).isEmpty());
			Assert.assertTrue(lines.get(4).isEmpty());
			Assert.assertTrue(lines.get(5).isEmpty());
			Assert.assertTrue(lines.get(6).isEmpty());
			Assert.assertTrue(lines.get(7).isEmpty());
			Assert.assertTrue(lines.get(8).isEmpty());
			Assert.assertTrue(lines.get(9).isEmpty());
			Assert.assertTrue(lines.get(10).isEmpty());
			Assert.assertTrue(lines.get(11).isEmpty());
			Assert.assertTrue(lines.get(12).isEmpty());
			Assert.assertTrue(lines.get(13).isEmpty());

			Assert.assertEquals("User 3", lines.get(14).get(0));
			Assert.assertEquals("Role 3", lines.get(14).get(1));
			Assert.assertEquals("", lines.get(14).get(2));
			Assert.assertEquals("", lines.get(14).get(3));
			Assert.assertEquals("", lines.get(14).get(4));
			Assert.assertEquals("", lines.get(14).get(5));
			Assert.assertEquals("Name 3", lines.get(14).get(6));

			Assert.assertEquals("User 4", lines.get(15).get(0));
			Assert.assertEquals("Role 4", lines.get(15).get(1));
			Assert.assertEquals("", lines.get(15).get(2));
			Assert.assertEquals("", lines.get(15).get(3));
			Assert.assertEquals("", lines.get(15).get(4));
			Assert.assertEquals("", lines.get(15).get(5));
			Assert.assertEquals("Name 4", lines.get(15).get(6));

			Assert.assertEquals("User 5", lines.get(16).get(0));
			Assert.assertEquals("Role 5", lines.get(16).get(1));
			Assert.assertEquals("", lines.get(16).get(2));
			Assert.assertEquals("", lines.get(16).get(3));
			Assert.assertEquals("", lines.get(16).get(4));
			Assert.assertEquals("", lines.get(16).get(5));
			Assert.assertEquals("Name 5", lines.get(16).get(6));

		}
	}

	@Test
	public void testParseUsers3() throws IOException
	{
		try (CSVParser reader = new CSVParser(new InputStreamReader(getClass().getResourceAsStream("Users3.csv"))))
		{
			List<List<String>> lines = new ArrayList<>();
			for (Optional<List<String>> line = reader.parseLine();
				line.isPresent(); line = reader.parseLine())
				lines.add(line.get());

			Assert.assertEquals(20, lines.size());

			Assert.assertEquals(7, lines.get(0).size());
			Assert.assertEquals("User 1", lines.get(0).get(0));
			Assert.assertEquals("Role 1", lines.get(0).get(1));
			Assert.assertEquals("", lines.get(0).get(2));
			Assert.assertEquals("", lines.get(0).get(3));
			Assert.assertEquals("", lines.get(0).get(4));
			Assert.assertEquals("", lines.get(0).get(5));
			Assert.assertEquals("Name 1", lines.get(0).get(6));

			Assert.assertEquals("User 2", lines.get(1).get(0));
			Assert.assertEquals("Role 2", lines.get(1).get(1));
			Assert.assertEquals("", lines.get(1).get(2));
			Assert.assertEquals("", lines.get(1).get(3));
			Assert.assertEquals("", lines.get(1).get(4));
			Assert.assertEquals("", lines.get(1).get(5));
			Assert.assertEquals("Name 2", lines.get(1).get(6));

			Assert.assertEquals("User 3", lines.get(2).get(0));
			Assert.assertEquals("Role 3", lines.get(2).get(1));
			Assert.assertEquals("", lines.get(2).get(2));
			Assert.assertEquals("", lines.get(2).get(3));
			Assert.assertEquals("", lines.get(2).get(4));
			Assert.assertEquals("", lines.get(2).get(5));
			Assert.assertEquals("Name 3", lines.get(2).get(6));

			Assert.assertEquals("User 4", lines.get(3).get(0));
			Assert.assertEquals("Role 4", lines.get(3).get(1));
			Assert.assertEquals("", lines.get(3).get(2));
			Assert.assertEquals("", lines.get(3).get(3));
			Assert.assertEquals("", lines.get(3).get(4));
			Assert.assertEquals("", lines.get(3).get(5));
			Assert.assertEquals("Name 4", lines.get(3).get(6));

			Assert.assertEquals("User 5", lines.get(4).get(0));
			Assert.assertEquals("Role 5", lines.get(4).get(1));
			Assert.assertEquals("", lines.get(4).get(2));
			Assert.assertEquals("", lines.get(4).get(3));
			Assert.assertEquals("", lines.get(4).get(4));
			Assert.assertEquals("", lines.get(4).get(5));
			Assert.assertEquals("Name 5", lines.get(4).get(6));

			Assert.assertTrue(lines.get(5).isEmpty());
			Assert.assertTrue(lines.get(6).isEmpty());
			Assert.assertTrue(lines.get(7).isEmpty());
			Assert.assertTrue(lines.get(8).isEmpty());
			Assert.assertTrue(lines.get(9).isEmpty());
			Assert.assertTrue(lines.get(10).isEmpty());
			Assert.assertTrue(lines.get(11).isEmpty());
			Assert.assertTrue(lines.get(12).isEmpty());
			Assert.assertTrue(lines.get(13).isEmpty());
			Assert.assertTrue(lines.get(14).isEmpty());
			Assert.assertTrue(lines.get(15).isEmpty());
			Assert.assertTrue(lines.get(16).isEmpty());
			Assert.assertTrue(lines.get(17).isEmpty());
			Assert.assertTrue(lines.get(18).isEmpty());
			Assert.assertTrue(lines.get(19).isEmpty());

		}
	}

	@Test
	public void testParseUsers4() throws IOException
	{
		try (CSVParser reader = new CSVParser(new InputStreamReader(getClass().getResourceAsStream("Users4.csv"))))
		{
			List<List<String>> lines = new ArrayList<>();
			for (Optional<List<String>> line = reader.parseLine();
				line.isPresent(); line = reader.parseLine())
				lines.add(line.get());

			Assert.assertEquals(6, lines.size());

			Assert.assertEquals(3, lines.get(0).size());
			Assert.assertEquals("User 1", lines.get(0).get(0));
			Assert.assertEquals("Role 1", lines.get(0).get(1));
			Assert.assertEquals("", lines.get(0).get(2));

			Assert.assertEquals(3, lines.get(1).size());
			Assert.assertEquals("User 2", lines.get(1).get(0));
			Assert.assertEquals("Role 2", lines.get(1).get(1));
			Assert.assertEquals("", lines.get(0).get(2));

			Assert.assertTrue(lines.get(5).isEmpty());

		}
	}

	@Test
	public void testParseUsers5() throws IOException
	{
		try (CSVParser reader = new CSVParser(new InputStreamReader(getClass().getResourceAsStream("Users5.csv"))))
		{
			List<List<String>> lines = new ArrayList<>();
			for (Optional<List<String>> line = reader.parseLine();
				line.isPresent(); line = reader.parseLine())
				lines.add(line.get());

			Assert.assertEquals(6, lines.size());

			Assert.assertEquals(3, lines.get(0).size());
			Assert.assertEquals("Use'r 1", lines.get(0).get(0));
			Assert.assertEquals("Role 1", lines.get(0).get(1));
			Assert.assertEquals("", lines.get(0).get(2));

			Assert.assertEquals(3, lines.get(1).size());
			Assert.assertEquals("User 2", lines.get(1).get(0));
			Assert.assertEquals("Role '2", lines.get(1).get(1));
			Assert.assertEquals("", lines.get(0).get(2));

			Assert.assertTrue(lines.get(5).isEmpty());

		}
	}

	@Test
	public void testParseNumbers() throws IOException
	{
		try (CSVParser reader = new CSVParser(new InputStreamReader(getClass().getResourceAsStream("Numbers.csv"))))
		{
			List<List<String>> lines = new ArrayList<>();
			for (Optional<List<String>> line = reader.parseLine();
				line.isPresent(); line = reader.parseLine())
				lines.add(line.get());

			Assert.assertEquals(35, lines.size());

		}
	}
}
