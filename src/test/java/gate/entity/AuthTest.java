package gate.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthTest
{

	@Test
	public void test01()
	{
		assertTrue(new Auth()
			.setAccess(Auth.Access.GRANT)
			.granted("module1", "screen1", "action1"));
	}

	@Test
	public void test02()
	{
		assertTrue(new Auth()
			.setAccess(Auth.Access.GRANT)
			.setModule("module1")
			.granted("module1", "screen1", "action1"));
	}

	@Test
	public void test05()
	{
		assertTrue(new Auth()
			.setAccess(Auth.Access.GRANT)
			.setAction("action1")
			.granted("module1", "screen1", "action1"));
	}

	@Test
	public void test07()
	{
		assertFalse(new Auth()
			.setAccess(Auth.Access.GRANT)
			.setAction("action1")
			.granted("module1", "screen1", "action2"));
	}

}
