package gate.type;

import gate.entity.Role;
import gate.entity.User;
import gate.sql.ColumnReference;
import gate.sql.annotation.Column;
import gate.annotation.Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColumnReferenceTest
{
	private static final Client CLIENT = new Client("Name",
			true, true, new Company("uuid"));

	private static final User USER = new User()
			.setName("Name")
			.setActive(true)
			.setRole(new Role().setId(ID.valueOf(1)));

	@Test
	public void testGetterProperty()
	{
		PropertyReference<User, String> property = User::getName;
		var column = ColumnReference.of(property);
		assertEquals("name", column.name());
		assertEquals("Name", column.extractor().apply(property.apply(USER)));
	}

	@Test
	public void testBooleanGetterProperty()
	{
		PropertyReference<User, Boolean> property = User::getActive;
		var column = ColumnReference.of(property);
		assertEquals("active", column.name());
		assertEquals(true, column.extractor().apply(property.apply(USER)));
	}

	@Test
	public void testRecordAccessorProperty()
	{
		PropertyReference<Client, String> property = Client::name;
		var column = ColumnReference.of(property);
		assertEquals("name", column.name());
		assertEquals("Name", column.extractor().apply(property.apply(CLIENT)));
	}

	@Test
	public void testRecordBooleanAccessorWithIsPrefix()
	{
		PropertyReference<Client, Boolean> property = Client::isAdmin;
		var column = ColumnReference.of(property);
		assertEquals("admin", column.name());
		assertEquals(true, column.extractor().apply(property.apply(CLIENT)));
	}

	@Test
	public void testEntityGetterReferenceUsesIdSuffix()
	{
		PropertyReference<User, Role> property = User::getRole;
		var column = ColumnReference.of(property);
		assertEquals("Role$id", column.name());
		assertEquals(ID.valueOf(1), column.extractor().apply(property.apply(USER)));
	}

	@Test
	public void testEntityGetterReferenceUsesCustomEntityKey()
	{
		PropertyReference<Client, Company> property = Client::company;
		var column = ColumnReference.of(property);
		assertEquals("Company$uuid", column.name());
		assertEquals("uuid", column.extractor().apply(property.apply(CLIENT)));
	}

	@Test
	public void testMethodColumnAnnotationOverridesConvention()
	{
		PropertyReference<AnnotatedUser, String> property = AnnotatedUser::getName;
		var column = ColumnReference.of(property);
		assertEquals("usr_name", column.name());
		assertEquals("Name", column.extractor().apply(property.apply(new AnnotatedUser("Name"))));
	}

	@Test
	public void testFieldColumnAnnotationOverridesConvention()
	{
		PropertyReference<FieldAnnotatedUser, String> property = FieldAnnotatedUser::getName;
		var column = ColumnReference.of(property);
		assertEquals("usr_name", column.name());
		assertEquals("Name", column.extractor().apply(property.apply(new FieldAnnotatedUser("Name"))));
	}

	@Test
	public void testMethodColumnAnnotationTakesPrecedenceOverFieldAnnotation()
	{
		PropertyReference<DoublyAnnotatedUser, String> property = DoublyAnnotatedUser::getName;
		var column = ColumnReference.of(property);
		assertEquals("getter_name", column.name());
		assertEquals("Name", column.extractor().apply(property.apply(new DoublyAnnotatedUser("Name"))));
	}

	@Test
	public void testMethodColumnAnnotationOverridesEntityReferenceConvention()
	{
		PropertyReference<AnnotatedReferenceUser, Role> property = AnnotatedReferenceUser::getRole;
		var column = ColumnReference.of(property);
		assertEquals("role_id", column.name());
		assertEquals(ID.valueOf(1), column.extractor().apply(property.apply(new AnnotatedReferenceUser(USER.getRole()))));
	}

	@Entity("uuid")
	public record Company(String uuid)
	{
	}

	public record Client(String name, boolean active, boolean isAdmin, Company company)
	{
	}

	public static class AnnotatedUser
	{
		private final String name;

		public AnnotatedUser(String name)
		{
			this.name = name;
		}

		@Column("usr_name")
		public String getName()
		{
			return name;
		}
	}

	public static class FieldAnnotatedUser
	{
		@Column("usr_name")
		private final String name;

		public FieldAnnotatedUser(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}

	public static class DoublyAnnotatedUser
	{
		@Column("field_name")
		private final String name;

		public DoublyAnnotatedUser(String name)
		{
			this.name = name;
		}

		@Column("getter_name")
		public String getName()
		{
			return name;
		}
	}

	public static class AnnotatedReferenceUser
	{
		private final Role role;

		public AnnotatedReferenceUser(Role role)
		{
			this.role = role;
		}

		@Column("role_id")
		public Role getRole()
		{
			return role;
		}
	}
}
