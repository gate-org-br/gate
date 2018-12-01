package gate.io;

import gate.annotation.SecurityKey;
import gate.entity.User;
import gate.type.ID;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class JsonTableTest
{

	private Table<User> table;
	private static final File USERS = new File("Users");
	private static final User USER0 = new User().setId(new ID(0)).setName("User 0");
	private static final User USER1 = new User().setId(new ID(1)).setName("User 1");
	private static final User USER2 = new User().setId(new ID(2)).setName("User 2");
	private static final User USER3 = new User().setId(new ID(3)).setName("User 3");
	private static final User USER4 = new User().setId(new ID(4)).setName("User 4");
	private static final User USER5 = new User().setId(new ID(5)).setName("User 5");
	private static final User USER6 = new User().setId(new ID(6)).setName("User 6");
	private static final User USER7 = new User().setId(new ID(7)).setName("User 7");
	private static final User USER8 = new User().setId(new ID(8)).setName("User 8");
	private static final User USER9 = new User().setId(new ID(9)).setName("User 9");
	private static final User USER10 = new User().setId(new ID(10)).setName("User 10");
	private static final User USER11 = new User().setId(new ID(11)).setName("User 11");

	private Table<ClassifiedUser> classifiedTable;
	private static final File CLASSIFIED_USERS = new File("ClassifiedUsers");
	private static final ClassifiedUser CLASSIFIED_USER0 = new ClassifiedUser().setId(new ID(0)).setName("User 0");
	private static final ClassifiedUser CLASSIFIED_USER1 = new ClassifiedUser().setId(new ID(1)).setName("User 1");
	private static final ClassifiedUser CLASSIFIED_USER2 = new ClassifiedUser().setId(new ID(2)).setName("User 2");
	private static final ClassifiedUser CLASSIFIED_USER3 = new ClassifiedUser().setId(new ID(3)).setName("User 3");
	private static final ClassifiedUser CLASSIFIED_USER4 = new ClassifiedUser().setId(new ID(4)).setName("User 4");
	private static final ClassifiedUser CLASSIFIED_USER5 = new ClassifiedUser().setId(new ID(5)).setName("User 5");
	private static final ClassifiedUser CLASSIFIED_USER6 = new ClassifiedUser().setId(new ID(6)).setName("User 6");
	private static final ClassifiedUser CLASSIFIED_USER7 = new ClassifiedUser().setId(new ID(7)).setName("User 7");
	private static final ClassifiedUser CLASSIFIED_USER8 = new ClassifiedUser().setId(new ID(8)).setName("User 8");
	private static final ClassifiedUser CLASSIFIED_USER9 = new ClassifiedUser().setId(new ID(9)).setName("User 9");
	private static final ClassifiedUser CLASSIFIED_USER10 = new ClassifiedUser().setId(new ID(10)).setName("User 10");
	private static final ClassifiedUser CLASSIFIED_USER11 = new ClassifiedUser().setId(new ID(11)).setName("User 11");

	@Before
	public void setUp()
	{
		table = Table.jsonConcurrent(User.class, USERS);
		table.insert(
				USER0,
				USER1,
				USER2,
				USER3,
				USER4,
				USER5,
				USER6,
				USER7,
				USER8,
				USER9);

		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		classifiedTable.insert(
				CLASSIFIED_USER0,
				CLASSIFIED_USER1,
				CLASSIFIED_USER2,
				CLASSIFIED_USER3,
				CLASSIFIED_USER4,
				CLASSIFIED_USER5,
				CLASSIFIED_USER6,
				CLASSIFIED_USER7,
				CLASSIFIED_USER8,
				CLASSIFIED_USER9);

	}

	@After
	public void tearDown()
	{
		table.drop();
		classifiedTable.drop();
	}

	@Test
	public void testInsert_CollectionClassified()
	{

		classifiedTable.insert(CLASSIFIED_USER10, CLASSIFIED_USER11);
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertEquals(classifiedTable.size(), 12);
	}

	@Test
	public void testInsert_GenericTypeClassified()
	{
		classifiedTable.insert(Arrays.asList(CLASSIFIED_USER10, CLASSIFIED_USER11));
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertEquals(classifiedTable.size(), 12);
	}

	@Test
	public void testDelete_PredicateClassified()
	{
		classifiedTable.delete(e -> e.getId().getValue() % 2 == 0);
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertEquals(classifiedTable.size(), 5);
	}

	@Test
	public void testDelete_CollectionClassified()
	{
		classifiedTable.delete(Arrays.asList(CLASSIFIED_USER1, CLASSIFIED_USER2));
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertEquals(classifiedTable.size(), 8);
	}

	@Test
	public void testDelete_GenericTypeClassified()
	{
		classifiedTable.delete(CLASSIFIED_USER1, CLASSIFIED_USER2);
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertEquals(classifiedTable.size(), 8);
	}

	@Test
	public void testIsEmptyClassified()
	{
		classifiedTable.delete(CLASSIFIED_USER1);
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertFalse(classifiedTable.isEmpty());

		classifiedTable.drop();
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertTrue(classifiedTable.isEmpty());
	}

	@Test
	public void testSizeClassified()
	{
		classifiedTable.delete(CLASSIFIED_USER1);
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertTrue(classifiedTable.size() == 9);

		classifiedTable.drop();
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertTrue(classifiedTable.size() == 0);
	}

	@Test
	public void testSelect_0argsClassified()
	{
		assertTrue(classifiedTable.select().isPresent());

		classifiedTable.drop();
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertFalse(classifiedTable.select().isPresent());
	}

	@Test
	public void testSelect_PredicateClassified()
	{
		assertTrue(classifiedTable.select(e -> e.getId().getValue() == 1).get().getName().equals("User 1"));
		assertFalse(classifiedTable.select(e -> e.getId().getValue() == 20).isPresent());
	}

	@Test
	public void testSelect_ComparatorClassified()
	{
		assertTrue(classifiedTable.select((a, b) -> b.getId().compareTo(a.getId()))
				.get().getName().equals("User 9"));
	}

	@Test
	public void testSeach_ComparatorClassified()
	{
		List<ClassifiedUser> users = classifiedTable.search((a, b) -> b.getId().compareTo(a.getId()));
		assertTrue(users.size() == 10);
		assertEquals(users.get(0).getName(), "User 9");
		assertEquals(users.get(1).getName(), "User 8");
		assertEquals(users.get(2).getName(), "User 7");
		assertEquals(users.get(3).getName(), "User 6");
		assertEquals(users.get(4).getName(), "User 5");
		assertEquals(users.get(5).getName(), "User 4");
		assertEquals(users.get(6).getName(), "User 3");
		assertEquals(users.get(7).getName(), "User 2");
		assertEquals(users.get(8).getName(), "User 1");
		assertEquals(users.get(9).getName(), "User 0");
	}

	@Test
	public void testSeach_Predicate_ComparatorClassified()
	{
		List<ClassifiedUser> users = classifiedTable.search(e -> e.getId().getValue() % 2 == 0,
				(a, b) -> b.getId().compareTo(a.getId()));
		assertTrue(users.size() == 5);
		assertEquals(users.get(0).getName(), "User 8");
		assertEquals(users.get(1).getName(), "User 6");
		assertEquals(users.get(2).getName(), "User 4");
		assertEquals(users.get(3).getName(), "User 2");
		assertEquals(users.get(4).getName(), "User 0");
	}

	@Test
	public void testSelect_Predicate_ComparatorClassified()
	{
		assertTrue(classifiedTable.select(e -> e.getId().getValue() % 2 == 0,
				(a, b) -> b.getId().compareTo(a.getId()))
				.get().getName().equals("User 8"));
	}

	@Test
	public void testSearch_0argsClassified()
	{
		assertTrue(classifiedTable.search().size() == 10);
	}

	@Test
	public void testSearch_PredicateClassified()
	{
		assertTrue(classifiedTable.search(e -> e.getId().getValue() % 2 == 0).size() == 5);
	}

	@Test
	public void testDropClassified()
	{
		classifiedTable.drop();
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertTrue(classifiedTable.isEmpty());
	}

	@Test
	public void testNewConcurrentInstanceClassified()
	{
		classifiedTable = Table.jsonConcurrent(ClassifiedUser.class, CLASSIFIED_USERS);
		assertTrue(classifiedTable.size() == 10);
	}

	@Test
	public void testInsert_Collection()
	{

		table.insert(USER10, USER11);
		table = Table.jsonConcurrent(User.class, USERS);
		assertEquals(table.size(), 12);
	}

	@Test
	public void testInsert_GenericType()
	{
		table.insert(Arrays.asList(USER10, USER11));
		table = Table.jsonConcurrent(User.class, USERS);
		assertEquals(table.size(), 12);
	}

	@Test
	public void testDelete_Predicate()
	{
		table.delete(e -> e.getId().getValue() % 2 == 0);
		table = Table.jsonConcurrent(User.class, USERS);
		assertEquals(table.size(), 5);
	}

	@Test
	public void testDelete_Collection()
	{
		table.delete(Arrays.asList(USER1, USER2));
		table = Table.jsonConcurrent(User.class, USERS);
		assertEquals(table.size(), 8);
	}

	@Test
	public void testDelete_GenericType()
	{
		table.delete(USER1, USER2);
		table = Table.jsonConcurrent(User.class, USERS);
		assertEquals(table.size(), 8);
	}

	@Test
	public void testIsEmpty()
	{
		table.delete(USER1);
		table = Table.jsonConcurrent(User.class, USERS);
		assertFalse(table.isEmpty());

		table.drop();
		table = Table.jsonConcurrent(User.class, USERS);
		assertTrue(table.isEmpty());
	}

	@Test
	public void testSize()
	{
		table.delete(USER1);
		table = Table.jsonConcurrent(User.class, USERS);
		assertTrue(table.size() == 9);

		table.drop();
		table = Table.jsonConcurrent(User.class, USERS);
		assertTrue(table.size() == 0);
	}

	@Test
	public void testSelect_0args()
	{
		assertTrue(table.select().isPresent());

		table.drop();
		table = Table.jsonConcurrent(User.class, USERS);
		assertFalse(table.select().isPresent());
	}

	@Test
	public void testSelect_Predicate()
	{
		assertTrue(table.select(e -> e.getId().getValue() == 1).get().getName().equals("User 1"));
		assertFalse(table.select(e -> e.getId().getValue() == 20).isPresent());
	}

	@Test
	public void testSelect_Comparator()
	{
		assertTrue(table.select((a, b) -> b.getId().compareTo(a.getId()))
				.get().getName().equals("User 9"));
	}

	@Test
	public void testSeach_Comparator()
	{
		List<User> users = table.search((a, b) -> b.getId().compareTo(a.getId()));
		assertTrue(users.size() == 10);
		assertEquals(users.get(0).getName(), "User 9");
		assertEquals(users.get(1).getName(), "User 8");
		assertEquals(users.get(2).getName(), "User 7");
		assertEquals(users.get(3).getName(), "User 6");
		assertEquals(users.get(4).getName(), "User 5");
		assertEquals(users.get(5).getName(), "User 4");
		assertEquals(users.get(6).getName(), "User 3");
		assertEquals(users.get(7).getName(), "User 2");
		assertEquals(users.get(8).getName(), "User 1");
		assertEquals(users.get(9).getName(), "User 0");
	}

	@Test
	public void testSeach_Predicate_Comparator()
	{
		List<User> users = table.search(e -> e.getId().getValue() % 2 == 0,
				(a, b) -> b.getId().compareTo(a.getId()));
		assertTrue(users.size() == 5);
		assertEquals(users.get(0).getName(), "User 8");
		assertEquals(users.get(1).getName(), "User 6");
		assertEquals(users.get(2).getName(), "User 4");
		assertEquals(users.get(3).getName(), "User 2");
		assertEquals(users.get(4).getName(), "User 0");
	}

	@Test
	public void testSelect_Predicate_Comparator()
	{
		assertTrue(table.select(e -> e.getId().getValue() % 2 == 0,
				(a, b) -> b.getId().compareTo(a.getId()))
				.get().getName().equals("User 8"));
	}

	@Test
	public void testSearch_0args()
	{
		assertTrue(table.search().size() == 10);
	}

	@Test
	public void testSearch_Predicate()
	{
		assertTrue(table.search(e -> e.getId().getValue() % 2 == 0).size() == 5);
	}

	@Test
	public void testDrop()
	{
		table.drop();
		table = Table.jsonConcurrent(User.class, USERS);
		assertTrue(table.isEmpty());
	}

	@Test
	public void testNewConcurrentInstance()
	{
		table = Table.jsonConcurrent(User.class, USERS);
		assertTrue(table.size() == 10);
	}

	@SecurityKey("/zgq2IL4H6tN4kRrzybBQA==")
	private static class ClassifiedUser extends gate.entity.User
	{

		private static final long serialVersionUID = 1L;

		public ClassifiedUser()
		{

		}

		@Override
		public ClassifiedUser setId(ID id)
		{
			return (ClassifiedUser) super.setId(id);
		}

		@Override
		public ClassifiedUser setName(String name)
		{
			return (ClassifiedUser) super.setName(name);
		}

	}
}
