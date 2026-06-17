package gate.lang.constructionStrategy;

import gate.annotation.Canonical;
import gate.error.ConversionException;
import gate.lang.property.Attribute;
import gate.lang.property.Property;
import mock.ConstructionMocks.AmbiguousConstructorMock;
import mock.ConstructionMocks.CanonicalConstructorMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ConstructionStrategyTest extends ConstructionStrategyTestSupport
{
	@Test
	void shouldThrowWhenConstructorAndFactoryMethodSelectionIsAmbiguous()
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(AmbiguousConstructorMock.class, "field1").getLastAttribute(), "value1",
				Property.getProperty(AmbiguousConstructorMock.class, "field2").getLastAttribute(), VALUE2,
				Property.getProperty(AmbiguousConstructorMock.class, "field3").getLastAttribute(), 3);

		Assertions.assertThrows(ConversionException.class,
				() -> ConstructionStrategy.newInstance(AmbiguousConstructorMock.class, attributes));
	}

	@Test
	void shouldUseCanonicalConstructorToResolveAmbiguity() throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(CanonicalConstructorMock.class, "field1").getLastAttribute(), "value1",
				Property.getProperty(CanonicalConstructorMock.class, "field2").getLastAttribute(), VALUE2,
				Property.getProperty(CanonicalConstructorMock.class, "field3").getLastAttribute(), 3);

		var result = (CanonicalConstructorMock)
				ConstructionStrategy.newInstance(CanonicalConstructorMock.class, attributes);

		Assertions.assertEquals("value1", result.getField1());
		Assertions.assertEquals(VALUE2, result.getField2());
		Assertions.assertEquals(3, result.getField3());
	}

	@Test
	void shouldPreferCanonicalConstructorOverExactConstructor()
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(ExactOverCanonicalMock.class, "name").getLastAttribute(), "Ana");

		var result = (ExactOverCanonicalMock)
				ConstructionStrategy.newInstance(ExactOverCanonicalMock.class, attributes);

		Assertions.assertEquals("Ana", result.getName());
		Assertions.assertEquals("canonical", result.getSource());
		Assertions.assertNull(result.getDescription());
	}

	public static class ExactOverCanonicalMock
	{
		private final String name;
		private final String description;
		private final String source;

		public ExactOverCanonicalMock(String name)
		{
			this.name = name;
			this.description = null;
			this.source = "exact";
		}

		@Canonical
		public ExactOverCanonicalMock(String name, String description)
		{
			this.name = name;
			this.description = description;
			this.source = "canonical";
		}

		public String getName()
		{
			return name;
		}

		public String getDescription()
		{
			return description;
		}

		public String getSource()
		{
			return source;
		}
	}
}
