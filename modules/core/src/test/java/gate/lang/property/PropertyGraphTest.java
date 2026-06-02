package gate.lang.property;

import gate.annotation.Discriminator;
import gate.annotation.Subtype;
import gate.error.ConversionException;
import gate.error.PropertyError;
import mock.ConstructionMocks;
import mock.IDMock;
import mock.RoleMock;
import mock.UserMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class PropertyGraphTest
{

	@Test
	public void testEntity()
	{
		var request = Map
				.of("id", 1,
						"name", "Ana",
						"role.id", IDMock.valueOf(2),
						"role.name", "Root");

		var result = (UserMock) PropertyGraph
				.of(UserMock.class, new ArrayList<>(request.keySet()))
				.populate(null, prop -> request.get(prop.toString()));

		Assertions.assertEquals(1, result.getId());
		Assertions.assertEquals("Ana", result.getName());
		Assertions.assertEquals(IDMock.valueOf(2), result.getRole().getId());
		Assertions.assertEquals("Root", result.getRole().getName());
	}

	@Test
	public void testBuildGraphFromFlatProperties()
	{
		var request = Map
				.of("name", "Ana",
						"role.name", "Admin",
						"role.manager.name", "Root",
						"role.manager.level", 10);

		var result = (UserMock) PropertyGraph
				.of(UserMock.class, new ArrayList<>(request.keySet()))
				.populate(null, prop -> request.get(prop.toString()));

		Assertions.assertEquals("Ana", result.getName());
		Assertions.assertEquals("Admin", result.getRole().getName());
		Assertions.assertEquals("Root", result.getRole().getManager().getName());
		Assertions.assertEquals(10, result.getRole().getManager().getLevel());
	}

	@Test
	public void testRecord()
	{
		var expected = new LineMock(new PointMock(1, 2), new PointMock(3, 4));

		var request = Map
				.of("start.x", 1,
						"start.y", 2,
						"end.x", 3,
						"end.y", 4);

		var result = PropertyGraph
				.of(LineMock.class, new ArrayList<>(request.keySet()))
				.populate(null, prop -> request.get(prop.toString()));

		Assertions.assertEquals(expected, result);
	}

	@Test
	public void testList()
	{
		var request = Map
				.of("contacts[0].value", "module1");

		var result = (UserMock) PropertyGraph
				.of(UserMock.class, new ArrayList<>(request.keySet()))
				.populate(null, prop -> request.get(prop.toString()));

		Assertions.assertEquals(1, result.getContacts().size());
		Assertions.assertEquals("module1", result.getContacts().get(0).getValue());
	}

	@Test
	public void testCollection()
	{
		var request = Map
				.of("contacts[].value", "module1");

		var result = (UserMock) PropertyGraph
				.of(UserMock.class, new ArrayList<>(request.keySet()))
				.populate(null, prop -> request.get(prop.toString()));

		Assertions.assertEquals(1, result.getContacts().size());
		Assertions.assertEquals("module1", result.getContacts().get(0).getValue());
	}

	@Test
	public void testIgnoreInvalidProperty()
	{
		var request = Map
				.of("name", "Ana");

		var properties = List.of("name", "doesNotExist");

		var result = (UserMock) PropertyGraph
				.of(UserMock.class, properties)
				.populate(null, prop -> request.get(prop.toString()));

		Assertions.assertEquals("Ana", result.getName());
	}

	@Test
	public void testUpdateExistingAnemicInstance()
	{
		var originalRole = new RoleMock().setName("Old Role");
		var original = new UserMock()
				.setName("Old")
				.setRole(originalRole);

		var request = Map
				.of("name", "New");

		var result = (UserMock) PropertyGraph
				.of(UserMock.class, new ArrayList<>(request.keySet()))
				.populate(original, prop -> request.get(prop.toString()));

		Assertions.assertSame(original, result);
		Assertions.assertEquals("New", result.getName());
		Assertions.assertSame(originalRole, result.getRole());
	}

	@Test
	public void testBuilderType()
	{
		var expected = ConstructionMocks.BuilderMock.builder()
				.field1("value1")
				.field2(LocalDate.of(2, 2, 2))
				.field3(3)
				.build();

		var result = (ConstructionMocks.BuilderMock) PropertyGraph.of(ConstructionMocks.BuilderMock.class,
						List.of("field1", "field2", "field3"))
				.populate(null, e -> switch (e.toString())
				{
					case "field1" -> "value1";
					case "field2" -> LocalDate.of(2, 2, 2);
					case "field3" -> 3;
					default -> throw new IllegalStateException("Unexpected value: " + e);
				});

		Assertions.assertEquals(expected.getField1(), result.getField1());
		Assertions.assertEquals(expected.getField2(), result.getField2());
		Assertions.assertEquals(expected.getField3(), result.getField3());
	}

	@Test
	public void testAmbiguousConstructor()
	{
		var request = Map
				.of("field1", "value1",
						"field2", LocalDate.of(2, 2, 2),
						"field3", 3);

		Assertions.assertThrows(ConversionException.class, () ->
				PropertyGraph
						.of(ConstructionMocks.AmbiguousConstructorMock.class, new ArrayList<>(request.keySet()))
						.populate(null, prop -> request.get(prop.toString())));
	}

	@Test
	public void testAmbiguousSealedProperty()
	{
		Assertions.assertThrows(PropertyError.class,
				() -> PropertyGraph.of(AmbiguousSealedParentMock.class, List.of("name")));
	}

	@Test
	public void testSharedSealedPropertyDeclaredByIntermediateSubtype()
	{
		Assertions.assertDoesNotThrow(
				() -> PropertyGraph.of(SharedSealedParentMock.class, List.of("name")));
	}

	@Test
	public void testDiscriminatorProperty()
	{
		Assertions.assertDoesNotThrow(
				() -> PropertyGraph.of(DiscriminatedParentMock.class, List.of("name")));
	}

	@Test
	public void testAmbiguousDiscriminatorProperty()
	{
		Assertions.assertThrows(PropertyError.class,
				() -> PropertyGraph.of(AmbiguousDiscriminatedParentMock.class, List.of("name")));
	}

	@Test
	public void testInvalidDiscriminatorSubtype()
	{
		Assertions.assertThrows(PropertyError.class,
				() -> PropertyGraph.of(InvalidDiscriminatedParentMock.class, List.of("name")));
	}

	public record PointMock(int x, int y) {}

	public record LineMock(PointMock start, PointMock end) {}

	static sealed class AmbiguousSealedParentMock
			permits AmbiguousSealedChildMock, AmbiguousSealedSiblingMock
	{
	}

	static final class AmbiguousSealedChildMock extends AmbiguousSealedParentMock
	{
		private String name;

		public String getName()
		{
			return name;
		}
	}

	static final class AmbiguousSealedSiblingMock extends AmbiguousSealedParentMock
	{
		private String name;

		public String getName()
		{
			return name;
		}
	}

	static sealed class SharedSealedParentMock
			permits SharedSealedNamedMock, SharedSealedOtherMock
	{
	}

	static sealed class SharedSealedNamedMock extends SharedSealedParentMock
			permits SharedSealedChildMock, SharedSealedSiblingMock
	{
		private String name;

		public String getName()
		{
			return name;
		}
	}

	static final class SharedSealedChildMock extends SharedSealedNamedMock
	{
	}

	static final class SharedSealedSiblingMock extends SharedSealedNamedMock
	{
	}

	static final class SharedSealedOtherMock extends SharedSealedParentMock
	{
	}

	static class DiscriminatedParentMock
	{
		@Discriminator
		private DiscriminatedTypeMock type;
	}

	enum DiscriminatedTypeMock
	{
		@Subtype(DiscriminatedNameMock.class)
		NAME,

		@Subtype(DiscriminatedCodeMock.class)
		CODE
	}

	static class DiscriminatedNameMock extends DiscriminatedParentMock
	{
		private String name;

		public String getName()
		{
			return name;
		}
	}

	static class DiscriminatedCodeMock extends DiscriminatedParentMock
	{
		private String code;

		public String getCode()
		{
			return code;
		}
	}

	static class AmbiguousDiscriminatedParentMock
	{
		@Discriminator
		private AmbiguousDiscriminatedTypeMock type;
	}

	enum AmbiguousDiscriminatedTypeMock
	{
		@Subtype(AmbiguousDiscriminatedNameMock.class)
		NAME,

		@Subtype(AmbiguousDiscriminatedSiblingMock.class)
		SIBLING
	}

	static class AmbiguousDiscriminatedNameMock extends AmbiguousDiscriminatedParentMock
	{
		private String name;

		public String getName()
		{
			return name;
		}
	}

	static class AmbiguousDiscriminatedSiblingMock extends AmbiguousDiscriminatedParentMock
	{
		private String name;

		public String getName()
		{
			return name;
		}
	}

	static class InvalidDiscriminatedParentMock
	{
		@Discriminator
		private InvalidDiscriminatedTypeMock type;
	}

	enum InvalidDiscriminatedTypeMock
	{
		@Subtype(DiscriminatedNameMock.class)
		NAME
	}

}
