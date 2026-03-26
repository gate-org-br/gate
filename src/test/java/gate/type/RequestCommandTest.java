package gate.type;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestCommandTest
{
	@Test
	public void shouldReturnDefault()
	{
		Assertions.assertEquals(RequestCommand.DEFAULT, RequestCommand.ofPath(null));
		Assertions.assertEquals(RequestCommand.DEFAULT, RequestCommand.ofPath("module"));
		Assertions.assertEquals(RequestCommand.DEFAULT, RequestCommand.ofPath("///"));
		Assertions.assertEquals(RequestCommand.DEFAULT, RequestCommand.ofPath("/*"));
		Assertions.assertEquals(RequestCommand.DEFAULT, RequestCommand.ofPath("/*/"));
		Assertions.assertEquals(RequestCommand.DEFAULT, RequestCommand.ofPath("/*/*"));
		Assertions.assertEquals(RequestCommand.DEFAULT, RequestCommand.ofPath("/*/*/"));
		Assertions.assertEquals(RequestCommand.DEFAULT, RequestCommand.ofPath("/*/*/*"));
		Assertions.assertEquals(RequestCommand.DEFAULT, RequestCommand.ofPath("/*/*/*/"));
	}

	@Test
	public void shouldReturnModulePath()
	{
		var expected = new RequestCommand("module", null, null);
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module//"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module///"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/*"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/*/"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/*/*"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/*/*/"));

	}

	@Test
	public void shouldReturnModuleScreenPath()
	{
		var expected = new RequestCommand("module", "screen", null);
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/screen"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/screen/"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/screen/*"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/screen/*/"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/screen//"));
	}

	@Test
	public void shouldReturnModuleScreenActionPath()
	{
		var expected = new RequestCommand("module", "screen", "action");
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/screen/action"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/screen/action/"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("/module/screen/action/1234"));
	}

	@Test
	public void shouldReturnScreenActionPath()
	{
		var expected = new RequestCommand(null, "screen", "action");
		Assertions.assertEquals(expected, RequestCommand.ofPath("/*/screen/action"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("//screen/action/"));
	}

	@Test
	public void shouldReturnActionPath()
	{
		var expected = new RequestCommand(null, null, "action");
		Assertions.assertEquals(expected, RequestCommand.ofPath("/*/*/action"));
		Assertions.assertEquals(expected, RequestCommand.ofPath("///action/"));
	}
}