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
		assertEquals("name", ref.property());
	}

	@Test
	public void testBooleanGetterProperty()
	{
		MethodReference<User, Boolean> ref = User::isActive;
		assertEquals("active", ref.property());
	}

	@Test
	public void testRecordAccessorProperty()
	{
		MethodReference<UserRecord, String> ref = UserRecord::name;
		assertEquals("name", ref.property());
	}

	@Test
	public void testRecordBooleanAccessorWithIsPrefix()
	{
		MethodReference<UserRecord, Boolean> ref = UserRecord::isAdmin;
		assertEquals("isAdmin", ref.property());
	}

	@Test
	public void testLambdaThrows()
	{
		MethodReference<User, String> ref = user -> user.getName();
		assertThrows(IllegalStateException.class, ref::property);
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
