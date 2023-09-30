package gate.io;

import gate.entity.User;
import gate.type.ID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PersistentSetTest
{

	@TempDir
	Path TEMP_DIR;

	private PersistentSet<User> persistentSet;
	private static final User USER0 = new User().setId(ID.valueOf(0)).setName("User 0");
	private static final User USER1 = new User().setId(ID.valueOf(1)).setName("User 1");
	private static final User USER2 = new User().setId(ID.valueOf(2)).setName("User 2");
	private static final User USER3 = new User().setId(ID.valueOf(3)).setName("User 3");
	private static final User USER4 = new User().setId(ID.valueOf(4)).setName("User 4");
	private static final User USER5 = new User().setId(ID.valueOf(5)).setName("User 5");
	private static final User USER6 = new User().setId(ID.valueOf(6)).setName("User 6");
	private static final User USER7 = new User().setId(ID.valueOf(7)).setName("User 7");
	private static final User USER8 = new User().setId(ID.valueOf(8)).setName("User 8");
	private static final User USER9 = new User().setId(ID.valueOf(9)).setName("User 9");
	private static final User USER10 = new User().setId(ID.valueOf(10)).setName("User 10");
	private static final User USER11 = new User().setId(ID.valueOf(11)).setName("User 11");

	@BeforeEach
	public void setUp()
	{
		persistentSet = PersistentSet.of(User.class, TEMP_DIR.resolve("test-persistent-set.json"));
		persistentSet.add(USER0);
		persistentSet.add(USER1);
		persistentSet.add(USER2);
		persistentSet.add(USER3);
		persistentSet.add(USER4);
		persistentSet.add(USER5);
		persistentSet.add(USER6);
		persistentSet.add(USER7);
		persistentSet.add(USER8);
		persistentSet.add(USER9);
	}

	@AfterEach
	public void tearDown()
	{
		persistentSet.clear();
	}

	@Test
	public void testAdd_Collection()
	{
		persistentSet.addAll(Arrays.asList(USER10, USER11));
		assertEquals(persistentSet.size(), 12);
	}

	@Test
	public void testClear()
	{
		persistentSet.clear();
		assertEquals(persistentSet.size(), 0);
		assertFalse(Files.exists(TEMP_DIR.resolve("test-persistent-set.json")));
	}

	@Test
	public void testIterator()
	{
		Iterator iterator = persistentSet.iterator();
		iterator.next();
		iterator.remove();
		assertEquals(persistentSet.size(), 9);
	}

	@Test
	public void testRemoveIf()
	{
		persistentSet.removeIf(e -> e.getName().equals("User 0"));
		assertEquals(persistentSet.size(), 9);
	}

}
