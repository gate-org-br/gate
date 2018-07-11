package gate.entity;

import org.junit.Test;
import static org.junit.Assert.*;

public class AuthTest
{

	@Test
	public void test01()
	{
		assertTrue(new Auth()
				.setMode(Auth.Mode.ALLOW)
				.allows(true, "module1", "screen1", "action1"));
	}

	@Test
	public void test02()
	{
		assertTrue(new Auth()
				.setMode(Auth.Mode.ALLOW)
				.setModule("module1")
				.allows(false, "module1", "screen1", "action1"));
	}

	@Test
	public void test03()
	{
		assertFalse(new Auth()
				.setMode(Auth.Mode.ALLOW)
				.setModule("module1")
				.allows(true, "module1", "screen1", "action1"));
	}

	@Test
	public void test04()
	{
		assertTrue(
				new Auth()
						.setMode(Auth.Mode.ALLOW)
						.setModule("module1")
						.setScreen("screen1")
						.setAction("action1")
						.allows(true, "module1", "screen1", "action1"));

	}

	@Test
	public void test05()
	{
		assertTrue(new Auth()
				.setMode(Auth.Mode.ALLOW)
				.setAction("action1")
				.allows(false, "module1", "screen1", "action1"));
	}

	@Test
	public void test06()
	{
		assertFalse(new Auth()
				.setMode(Auth.Mode.ALLOW)
				.setAction("action1")
				.allows(true, "module1", "screen1", "action1"));
	}

	@Test
	public void test07()
	{
		assertFalse(new Auth()
				.setMode(Auth.Mode.ALLOW)
				.setAction("action1")
				.allows(false, "module1", "screen1", "action2"));
	}

}
