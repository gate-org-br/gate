package gate.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PropertyReferenceTest
{

	@Test
	public void testGetterProperty()
	{
		PropertyReference<User, String> ref = User::getName;
		assertEquals("name", PropertyReference.property(ref));
	}

	@Test
	public void testBooleanGetterProperty()
	{
		PropertyReference<User, Boolean> ref = User::isActive;
		assertEquals("active", PropertyReference.property(ref));
	}

	@Test
	public void testRecordAccessorProperty()
	{
		PropertyReference<UserRecord, String> ref = UserRecord::name;
		assertEquals("name", PropertyReference.property(ref));
	}

	@Test
	public void testRecordBooleanAccessorWithIsPrefix()
	{
		PropertyReference<UserRecord, Boolean> ref = UserRecord::isAdmin;
		assertEquals("isAdmin", PropertyReference.property(ref));
	}

	@Test
	public void testLambdaThrows()
	{
		PropertyReference<User, String> ref = user -> user.getName();
		assertThrows(IllegalStateException.class, () -> PropertyReference.property(ref));
	}

	private static class User
	{

		private String name;
		private boolean active;

		public String getName()
		{
			return name;
		}

		public boolean isActive()
		{
			return active;
		}
	}

	private record UserRecord(String name, boolean active, boolean isAdmin)
	{
	}
}
