package gate.type;

import gate.annotation.Entity;
import gate.sql.ColumnReference;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertyReferenceTest
{

	@Test
	public void testGetterProperty()
	{
		PropertyReference<User, String> ref = User::getName;
		assertEquals("name", ColumnReference.of(ref));
	}

	@Test
	public void testBooleanGetterProperty()
	{
		PropertyReference<User, Boolean> ref = User::isActive;
		assertEquals("active", ColumnReference.of(ref));
	}

	@Test
	public void testRecordAccessorProperty()
	{
		PropertyReference<UserRecord, String> ref = UserRecord::name;
		assertEquals("name", ColumnReference.of(ref));
	}

	@Test
	public void testRecordBooleanAccessorWithIsPrefix()
	{
		PropertyReference<UserRecord, Boolean> ref = UserRecord::isAdmin;
		assertEquals("admin", ColumnReference.of(ref));
	}

	@Test
	public void testEntityGetterReferenceUsesIdSuffix()
	{
		PropertyReference<User, Role> ref = User::getRole;
		assertEquals("Role$id", ColumnReference.of(ref));
	}

	@Test
	public void testEntityGetterReferenceUsesCustomEntityKey()
	{
		PropertyReference<User, Company> ref = User::getCompany;
		assertEquals("Company$uuid", ColumnReference.of(ref));
	}

	@Test
	public void testEntityMethodWithoutGetUsesEntityReference()
	{
		PropertyReference<UserRecord, Company> ref = UserRecord::company;
		assertEquals("Company$uuid", ColumnReference.of(ref));
	}

	private static class User
	{

		private String name;
		private boolean active;
		private Role role;
		private Company company;

		public String getName()
		{
			return name;
		}

		public boolean isActive()
		{
			return active;
		}

		public Role getRole()
		{
			return role;
		}

		public Company getCompany()
		{
			return company;
		}
	}

	@Entity("id")
	public record Role(ID id)
	{
	}

	@Entity("uuid")
	public record Company(String uuid)
	{
	}

	public record UserRecord(String name, boolean active, boolean isAdmin, Company company)
	{
	}
}
