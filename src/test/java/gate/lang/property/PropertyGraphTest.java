package gate.lang.property;

import gate.entity.Auth;
import gate.entity.Role;
import gate.entity.User;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.type.ID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class PropertyGraphTest
{

	@Test
	public void testEntity() throws BadRequestException
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
	public void testRecord() throws BadRequestException
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
	public void testList() throws BadRequestException
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
	public void testCollection() throws BadRequestException
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

	@Test
	public void testIgnoreInvalidProperty() throws BadRequestException
	{
		var request = Map
				.of("name", "Ana");

		var properties = List.of("name", "doesNotExist");

		var result = (User) PropertyGraph
				.of(User.class, properties)
				.get(null, prop -> request.get(prop.toString()));

		Assertions.assertEquals("Ana", result.getName());
	}

	@Test
	public void testUpdateExistingAnemicInstance() throws BadRequestException
	{
		var originalRole = new Role().setName("Old Role");
		var original = new User()
				.setName("Old")
				.setRole(originalRole);

		var request = Map
				.of("name", "New");

		var result = (User) PropertyGraph
				.of(User.class, new ArrayList<>(request.keySet()))
				.get(original, prop -> request.get(prop.toString()));

		Assertions.assertSame(original, result);
		Assertions.assertEquals("New", result.getName());
		Assertions.assertSame(originalRole, result.getRole());
	}

	@Test
	public void testBuilderType() throws BadRequestException
	{
		var request = Map
				.of("name", "Builder Name",
						"age", 33);

		var previous = BuilderValue
				.builder()
				.name("old")
				.age(1)
				.build();

		var result = (BuilderValue) PropertyGraph
				.of(BuilderValue.class, new ArrayList<>(request.keySet()))
				.get(previous, prop -> request.get(prop.toString()));

		Assertions.assertEquals("Builder Name", result.getName());
		Assertions.assertEquals(33, result.getAge());
	}

	@Test
	public void testCanonicalConstructorAmbiguous() throws BadRequestException
	{
		var request = Map
				.of("arg0", "A",
						"arg1", 1);

		var ex = Assertions.assertThrows(ConversionException.class, () ->
				PropertyGraph
						.of(AmbiguousCanonical.class, new ArrayList<>(request.keySet()))
						.get(null, prop -> request.get(prop.toString())));
	}

	@Test
	public void testCanonicalConstructorAmbiguousWithoutGetter() throws BadRequestException
	{
		var request = Map
				.of("arg0", "A",
						"arg1", 1);

		var ex = Assertions.assertThrows(ConversionException.class, () ->
		{
			PropertyGraph
					.of(AmbiguousCanonicalWithoutGetter.class, new ArrayList<>(request.keySet()))
					.get(null, prop -> request.get(prop.toString()));
		});
	}

	record Point(int x, int y)
	{
	}

	record Line(Point start, Point end)
	{
	}

	static class BuilderValue
	{
		private final String name;
		private final Integer age;

		private BuilderValue(Builder builder)
		{
			this.name = builder.name;
			this.age = builder.age;
		}

		public static Builder builder()
		{
			return new Builder();
		}

		public String getName()
		{
			return name;
		}

		public Integer getAge()
		{
			return age;
		}

		static class Builder
		{
			private String name;
			private Integer age;

			public Builder name(String name)
			{
				this.name = name;
				return this;
			}

			public Builder age(Integer age)
			{
				this.age = age;
				return this;
			}

			public BuilderValue build()
			{
				return new BuilderValue(this);
			}
		}
	}

	static class AmbiguousCanonical
	{
		private final String arg0;
		private final Integer arg1;

		public AmbiguousCanonical(String arg0, Integer arg1)
		{
			this.arg0 = arg0;
			this.arg1 = arg1;
		}

		public AmbiguousCanonical(Object arg0, Number arg1)
		{
			this.arg0 = String.valueOf(arg0);
			this.arg1 = arg1 == null ? null : arg1.intValue();
		}

		public String getArg0()
		{
			return arg0;
		}

		public Integer getArg1()
		{
			return arg1;
		}
	}

	static class AmbiguousCanonicalWithoutGetter
	{
		private final String arg0;
		private final Integer arg1;

		public AmbiguousCanonicalWithoutGetter(String arg0, Integer arg1)
		{
			this.arg0 = arg0;
			this.arg1 = arg1;
		}

		public AmbiguousCanonicalWithoutGetter(Object arg0, Number arg1)
		{
			this.arg0 = String.valueOf(arg0);
			this.arg1 = arg1 == null ? null : arg1.intValue();
		}
	}
}
