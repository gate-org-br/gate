package gate.lang.property;

import gate.entity.Role;
import gate.error.BadRequestException;
import gate.type.ID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import gate.entity.Auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gate.entity.User;

class PropertyGraphTest
{

	@Test
	public void testEntity() throws BadRequestException, IOException, ReflectiveOperationException
	{
		var expected = new User()
				.setId(ID.valueOf(1))
				.setName("Ana")
				.setRole(new Role()
						.setId(ID.valueOf(2))
						.setName("Root"));

		var request = Map
				.of("id", ID.valueOf(1),
						"name", "Ana",
						"role.id", ID.valueOf(2),
						"role.name", "Root");


		var result = PropertyGraph
				.of(User.class, new ArrayList<>((request.keySet())))
				.get(null, prop -> request.get(prop.toString()));

		Assertions.assertEquals(expected, result);
	}

	@Test
	public void testRecord() throws BadRequestException, IOException, ReflectiveOperationException
	{
		var expected = new Line(new Point(1, 2), new Point(3, 4));

		var request = Map
				.of("start.x", 1,
						"start.y", 2,
						"end.x", 3,
						"end.y", 4);

		var result = PropertyGraph
				.of(Line.class, new ArrayList<>((request.keySet())))
				.get(null, prop -> request.get(prop.toString()));


		Assertions.assertEquals(expected, result);
	}

	@Test
	public void testList() throws BadRequestException, IOException, ReflectiveOperationException
	{
		var expected = new User()
				.setAuths(List.of(new Auth().setModule("module1")));

		var request = Map
				.of("auths[0].module", "module1");

		var result = PropertyGraph
				.of(User.class, new ArrayList<>((request.keySet())))
				.get(null, prop -> request.get(prop.toString()));

		Assertions.assertEquals(expected, result);
	}

	@Test
	public void testCollection() throws BadRequestException, IOException, ReflectiveOperationException
	{
		var expected = new User()
				.setAuths(List.of(new Auth().setModule("module1")));

		var request = Map
				.of("auths[].module", "module1");

		var result = PropertyGraph
				.of(User.class, new ArrayList<>((request.keySet())))
				.get(null, prop -> request.get(prop.toString()));

		Assertions.assertEquals(expected, result);
	}

	private static record Point(int x, int y)
	{
	}

	private static record Line(Point start, Point end)
	{
	}
}