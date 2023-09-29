package gate.entity;

import gate.error.AppException;
import gate.type.Hierarchy;
import gate.type.ID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RoleTest
{

	Role master1 = new Role()
		.setMaster(true)
		.setId(ID.valueOf(1))
		.setName("Master 1");

	Role master11 = new Role()
		.setParent(master1)
		.setMaster(true)
		.setId(ID.valueOf(11))
		.setName("Master 11");

	Role master111 = new Role()
		.setParent(master11)
		.setMaster(true)
		.setId(ID.valueOf(111))
		.setName("Master 111");

	Role master112 = new Role()
		.setParent(master1)
		.setMaster(true)
		.setId(ID.valueOf(112))
		.setName("Master 112");

	Role detail12 = new Role()
		.setParent(master1)
		.setMaster(false)
		.setId(ID.valueOf(12))
		.setName("Detail 12");

	Role detail121 = new Role()
		.setParent(detail12)
		.setMaster(false)
		.setId(ID.valueOf(121))
		.setName("Detail 121");

	List<Role> roles = new ArrayList<>();

	@BeforeEach
	public void setUp() throws AppException
	{
		roles.add(master1);

		roles.add(master11);

		roles.add(master111);

		roles.add(master112);

		roles.add(detail12);

		roles.add(detail121);

		Hierarchy.setup(roles);
	}

	@Test
	public void testMasters()
	{
		assertEquals(Arrays.asList(master1,
			master11,
			master111,
			master112),
			master1.masterStream().collect(Collectors.toList()));
	}

	@Test
	public void testSlaves()
	{
		assertEquals(Arrays.asList(master1, detail12,
			detail121),
			master1.slaveStream().collect(Collectors.toList()));
	}

	@Test
	public void testIsMasterOf()
	{
		assertTrue(master1.isMasterOf(detail12));
		assertTrue(master1.isMasterOf(detail121));
		assertFalse(master1.isMasterOf(master11));
		assertFalse(master1.isMasterOf(master111));
		assertFalse(master1.isMasterOf(master112));

	}

	@Test
	public void testIsSlaveOf()
	{
		assertTrue(detail12.isSlaveOf(master1));
		assertTrue(detail121.isSlaveOf(master1));
		assertFalse(master11.isSlaveOf(master1));
		assertFalse(master111.isSlaveOf(master1));
		assertFalse(master112.isSlaveOf(master1));
	}

}
