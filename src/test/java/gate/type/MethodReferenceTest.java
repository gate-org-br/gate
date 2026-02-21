package gate.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MethodReferenceTest
{

	@Test
	public void testGetterProperty()
	{
		MethodReference<User, String> ref = User::getName;
		assertEquals("name", MethodReference.property(ref));
	}

	@Test
	public void testBooleanGetterProperty()
	{
		MethodReference<User, Boolean> ref = User::isActive;
		assertEquals("active", MethodReference.property(ref));
	}

	@Test
	public void testRecordAccessorProperty()
	{
		MethodReference<UserRecord, String> ref = UserRecord::name;
		assertEquals("name", MethodReference.property(ref));
	}

	@Test
	public void testRecordBooleanAccessorWithIsPrefix()
	{
		MethodReference<UserRecord, Boolean> ref = UserRecord::isAdmin;
		assertEquals("isAdmin", MethodReference.property(ref));
	}

	@Test
	public void testLambdaThrows()
	{
		MethodReference<User, String> ref = user -> user.getName();
		assertThrows(IllegalStateException.class, () -> MethodReference.property(ref));
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
