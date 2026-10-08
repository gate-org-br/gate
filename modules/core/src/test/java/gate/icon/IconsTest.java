package gate.icon;

import gate.annotation.Icon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

@Icon("gate.type.LocalDateInterval")
public class IconsTest
{

	@Test
	public void testCode()
	{
		assertEquals("2003", Icons.getInstance().get("2003").orElseThrow().getCode());
		assertEquals("1001", Icons.getInstance().get("1001").orElseThrow().getCode());
		assertEquals("1000", Icons.getInstance().get("1000").orElseThrow().getCode());
	}

	@Test
	public void testName()
	{
		assertEquals("select", Icons.getInstance().get("select").orElseThrow().getName());
		assertEquals("insert", Icons.getInstance().get("insert").orElseThrow().getName());
		assertEquals("update", Icons.getInstance().get("update").orElseThrow().getName());
	}

}