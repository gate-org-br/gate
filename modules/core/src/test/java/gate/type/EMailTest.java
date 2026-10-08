package gate.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EMailTest
{
	@Test
	public void shouldNormalizeEmail()
	{
		assertEquals("user.name+tag@example.com", EMail.valueOf("  User.Name+Tag@Example.COM ").toString());
	}

	@Test
	public void shouldAcceptPracticalEmails()
	{
		assertTrue(EMail.validate("user@example.com"));
		assertTrue(EMail.validate("user.name+tag@example.co.uk"));
		assertTrue(EMail.validate("user_name-1@example-mail.com"));
	}

	@Test
	public void shouldRejectUnsafeOrInvalidEmails()
	{
		assertFalse(EMail.validate("a@b"));
		assertFalse(EMail.validate("user@<script>"));
		assertFalse(EMail.validate("\"user\"@example.com"));
		assertFalse(EMail.validate("user@example.com;alert(1)"));
		assertFalse(EMail.validate("user name@example.com"));
	}

	@Test
	public void shouldRejectUnsafeOrInvalidConstruction()
	{
		assertThrows(IllegalArgumentException.class, () -> EMail.valueOf("a@b"));
		assertThrows(IllegalArgumentException.class, () -> EMail.valueOf("\"user\"@example.com"));
		assertThrows(IllegalArgumentException.class, () -> EMail.valueOf("user@<script>"));
	}
}