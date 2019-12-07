package gate.util;

import gate.error.AppException;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

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

		Assert.assertEquals("gate.entity.User", parameters.get(ENTITY).orElseThrow());
		Assert.assertEquals("_generate", parameters.get(DAO).orElseThrow());
		Assert.assertEquals("gate.modulos", parameters.get(VIEW).orElseThrow());
	}

	@Test
	public void testToString() throws AppException, ParseException
	{
		String expected = "-d, --dao: Dao = _generate";
		String result = DAO.toString();
		Assert.assertEquals(expected, result);
	}
}
