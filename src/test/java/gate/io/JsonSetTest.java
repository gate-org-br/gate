package gate.io;

import gate.entity.User;
import gate.type.ID;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class JsonSetTest
{

	private PersistentSet<User> table;
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

	@Before
	public void setUp()
	{
		table = PersistentSet.of(User.class, USERS);
		table.add(USER0);
		table.add(USER1);
		table.add(USER2);
		table.add(USER3);
		table.add(USER4);
		table.add(USER5);
		table.add(USER6);
		table.add(USER7);
		table.add(USER8);
		table.add(USER9);
		table.commit();
	}

	@After
	public void tearDown()
	{
		table.clear();
		table.commit();
	}

	@Test
	public void testAdd_Collection()
	{
		table.addAll(Arrays.asList(USER10, USER11));
		table.commit();
		table.rollback();
		assertEquals(table.size(), 12);
	}

	@Test
	public void testClear()
	{
		table.clear();
		table.commit();
		table.rollback();
		assertEquals(table.size(), 0);
		Assert.assertFalse(USERS.exists());
	}

	@Test
	public void testIterator()
	{
		Iterator iterator = table.iterator();
		iterator.next();
		iterator.remove();
		table.commit();
		table.rollback();
		assertEquals(table.size(), 9);
	}

	@Test
	public void testRemoveIf()
	{
		table.removeIf(e -> e.getName().equals("User 0"));
		table.commit();
		table.rollback();
		assertEquals(table.size(), 9);
	}

}
