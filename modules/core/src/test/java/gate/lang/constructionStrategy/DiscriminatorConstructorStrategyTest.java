package gate.lang.constructionStrategy;

import gate.annotation.Discriminator;
import gate.annotation.Subtype;
import gate.error.ConstructionException;
import gate.lang.property.Attribute;
import gate.lang.property.Property;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class DiscriminatorConstructorStrategyTest extends ConstructionStrategyTestSupport
{
	@Test
	void shouldUseDiscriminatorToSelectSubtype()
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(DiscriminatedParentMock.class, "type").getLastAttribute(),
				DiscriminatedTypeMock.NAME,
				Property.getProperty(DiscriminatedNameMock.class, "name").getLastAttribute(),
				"Ana");

		var result = (DiscriminatedParentMock)
				ConstructionStrategy.newInstance(DiscriminatedParentMock.class, attributes);

		Assertions.assertInstanceOf(DiscriminatedNameMock.class, result);
		Assertions.assertEquals(DiscriminatedTypeMock.NAME, result.getType());
		Assertions.assertEquals("Ana", ((DiscriminatedNameMock) result).getName());
	}

	@Test
	void shouldKeepDiscriminatorSetBySubtypeConstructor()
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(ReadOnlyDiscriminatedParentMock.class, "type").getLastAttribute(),
				ReadOnlyDiscriminatedTypeMock.NAME,
				Property.getProperty(ReadOnlyDiscriminatedNameMock.class, "name").getLastAttribute(), "Ana");

		var result = (ReadOnlyDiscriminatedParentMock)
				ConstructionStrategy.newInstance(ReadOnlyDiscriminatedParentMock.class, attributes);

		Assertions.assertInstanceOf(ReadOnlyDiscriminatedNameMock.class, result);
		Assertions.assertEquals(ReadOnlyDiscriminatedTypeMock.NAME, result.getType());
		Assertions.assertEquals("Ana", ((ReadOnlyDiscriminatedNameMock) result).getName());
	}

	@Test
	void shouldRequireDiscriminatorWhenTypeHasDiscriminator()
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(DiscriminatedNameMock.class, "name").getLastAttribute(), "Ana");

		Assertions.assertThrows(ConstructionException.class,
				() -> ConstructionStrategy.newInstance(DiscriminatedParentMock.class, attributes));
	}

	@Test
	void shouldReturnNullWhenOnlyDiscriminatorIsProvided() throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(DiscriminatedParentMock.class, "type").getLastAttribute(),
				DiscriminatedTypeMock.NAME);

		var result = ConstructionStrategy.newInstance(DiscriminatedParentMock.class, attributes);

		Assertions.assertNull(result);
	}

	@Test
	void shouldFailWhenDiscriminatorSelectsSubtypeThatDoesNotKnowAttribute()
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(DiscriminatedParentMock.class, "type").getLastAttribute(),
				DiscriminatedTypeMock.CODE,
				Property.getProperty(DiscriminatedNameMock.class, "name").getLastAttribute(),
				"Ana");

		Assertions.assertThrows(ConstructionException.class,
				() -> ConstructionStrategy.newInstance(DiscriminatedParentMock.class, attributes));
	}

	static class DiscriminatedParentMock
	{
		@Discriminator
		private DiscriminatedTypeMock type;

		public DiscriminatedTypeMock getType()
		{
			return type;
		}
	}

	enum DiscriminatedTypeMock
	{
		@Subtype(DiscriminatedNameMock.class)
		NAME,

		@Subtype(DiscriminatedCodeMock.class)
		CODE,

		@Subtype(DiscriminatedDescriptionMock.class)
		DESCRIPTION
	}

	static class DiscriminatedNameMock extends DiscriminatedParentMock
	{
		private final String name;

		public DiscriminatedNameMock(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}

	static class DiscriminatedCodeMock extends DiscriminatedParentMock
	{
		private final Integer code;

		public DiscriminatedCodeMock(Integer code)
		{
			this.code = code;
		}

		public Integer getCode()
		{
			return code;
		}
	}

	static class DiscriminatedDescriptionMock extends DiscriminatedParentMock
	{
		private String description;

		public DiscriminatedDescriptionMock()
		{
		}

		public String getDescription()
		{
			return description;
		}

		public void setDescription(String description)
		{
			this.description = description;
		}
	}

	static class ReadOnlyDiscriminatedParentMock
	{
		@Discriminator
		private final ReadOnlyDiscriminatedTypeMock type;

		protected ReadOnlyDiscriminatedParentMock(ReadOnlyDiscriminatedTypeMock type)
		{
			this.type = type;
		}

		public ReadOnlyDiscriminatedTypeMock getType()
		{
			return type;
		}
	}

	enum ReadOnlyDiscriminatedTypeMock
	{
		@Subtype(ReadOnlyDiscriminatedNameMock.class)
		NAME
	}

	static class ReadOnlyDiscriminatedNameMock extends ReadOnlyDiscriminatedParentMock
	{
		private final String name;

		public ReadOnlyDiscriminatedNameMock(String name)
		{
			super(ReadOnlyDiscriminatedTypeMock.NAME);
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}
}