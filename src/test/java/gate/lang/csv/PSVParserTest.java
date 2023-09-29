package gate.lang.csv;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PSVParserTest
{

	@Test
	public void testUsers1() throws IOException
	{
		try (PSVParser parser = PSVParser.of(getClass().getResource("PSVParserTest/Users1.dat"), 0, 6, 12, 12, 12, 12, 12))
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
			assertEquals(List.of("User 4", "Role 4", "", "", "", "", "Name 4"), lines.get(index++));
		}
	}
}
