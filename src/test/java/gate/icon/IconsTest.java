package gate.icon;

import gate.annotation.Icon;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

@Icon("gate.type.Date")
public class IconsTest
{

	@Test
	public void testCode()
	{
		assertEquals("2003", Icons.getInstance().get("2003").get().getCode());
		assertEquals("1001", Icons.getInstance().get("1001").get().getCode());
		assertEquals("1000", Icons.getInstance().get("1000").get().getCode());
	}

	@Test
	public void testName()
	{
		assertEquals("select", Icons.getInstance().get("select").get().getName());
		assertEquals("insert", Icons.getInstance().get("insert").get().getName());
		assertEquals("update", Icons.getInstance().get("update").get().getName());
	}

}
