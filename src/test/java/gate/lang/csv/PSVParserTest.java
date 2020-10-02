package gate.lang.csv;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class PSVParserTest
{

	@Test
	public void testUsers1() throws IOException
	{
		try (PSVParser parser = PSVParser.of(getClass().getResource("PSVParserTest/Users1.dat"), 0, 6, 12, 12, 12, 12, 12))
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
}