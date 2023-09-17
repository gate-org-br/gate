package gate.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SecuritySessionsTest
{

	private SecuritySessions securitySessions;

	@Before
	public void setUp()
	{
		securitySessions = SecuritySessions.of(100);
	}

	@Test
	public void testCreateAndCheckSession()
	{
		SecuritySessions.SecuritySession session = securitySessions.create();
		Assert.assertNotNull(session);
		Assert.assertTrue(securitySessions.check(session.state().toString(),
			session.nonce().toString()));
		Assert.assertFalse(securitySessions.check(session.state().toString(),
			session.nonce().toString()));
	}

	@Test
	public void testCreateAndCheckInvalidSession()
	{
		SecuritySessions.SecuritySession session = securitySessions.create();

		Assert.assertNotNull(session);
		Assert.assertFalse(securitySessions.check(session.state().toString(),
			session.state().toString()));
	}

	@Test
	public void testTimeout() throws InterruptedException
	{
		SecuritySessions.SecuritySession session = securitySessions.create();
		Thread.sleep(200);
		Assert.assertFalse(securitySessions.check(session.state().toString(),
			session.nonce().toString()));
	}
}
