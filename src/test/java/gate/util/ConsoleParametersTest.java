package gate.util;

import gate.error.AppException;
import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ConsoleParametersTest
{

	private static final ConsoleParameters.Flag DAO
		= ConsoleParameters.Flag.builder().shortcut("-d")
			.defaultValue("_generate")
			.longname("--dao").description("Dao").build();

	private static final ConsoleParameters.Flag VIEW
		= ConsoleParameters.Flag.builder().shortcut("-v")
			.defaultValue("_generate")
			.longname("--view").description("View").build();

	private static final ConsoleParameters.Flag ENTITY
		= ConsoleParameters.Flag.builder().shortcut("-e")
			.longname("--entity").description("Entity").build();

	private static final String[] ARGS = new String[]
	{
		"gate",
		"-e=gate.entity.User",
		"-d",
		"-v=gate.modulos"
	};

	@Test
	public void testParse() throws AppException, ParseException
	{
		ConsoleParameters parameters = ConsoleParameters.parse(ARGS, DAO, VIEW, ENTITY);

		assertEquals("gate.entity.User", parameters.get(ENTITY).orElseThrow());
		assertEquals("_generate", parameters.get(DAO).orElseThrow());
		assertEquals("gate.modulos", parameters.get(VIEW).orElseThrow());
	}

	@Test
	public void testToString() throws AppException, ParseException
	{
		String expected = "-d, --dao: Dao = _generate";
		String result = DAO.toString();
		assertEquals(expected, result);
	}
}
