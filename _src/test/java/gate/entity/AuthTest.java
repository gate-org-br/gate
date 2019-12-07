package gate.entity;

import static org.junit.Assert.*;
import org.junit.Test;

public class AuthTest
{

	@Test
	public void test01()
	{
		assertTrue(new Auth()
			.setMode(Auth.Mode.ALLOW)
			.allows("module1", "screen1", "action1"));
	}

	@Test
	public void test02()
	{
		assertTrue(new Auth()
			.setMode(Auth.Mode.ALLOW)
			.setModule("module1")
			.allows("module1", "screen1", "action1"));
	}

	@Test
	public void test05()
	{
		assertTrue(new Auth()
			.setMode(Auth.Mode.ALLOW)
			.setAction("action1")
			.allows("module1", "screen1", "action1"));
	}

	@Test
	public void test07()
	{
		assertFalse(new Auth()
			.setMode(Auth.Mode.ALLOW)
			.setAction("action1")
			.allows("module1", "screen1", "action2"));
	}

}
