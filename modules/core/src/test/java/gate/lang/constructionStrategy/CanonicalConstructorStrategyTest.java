package gate.lang.constructionStrategy;

import gate.annotation.Canonical;
import gate.error.ConstructionException;
import gate.lang.property.Attribute;
import gate.lang.property.Property;
import mock.ConstructionMocks.PrimitiveConstructorMock;
import mock.ConstructionMocks.SingleConstructorMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

class CanonicalConstructorStrategyTest extends ConstructionStrategyTestSupport
{
	@Test
	void shouldConstructCanonicalConstructorTypeFromAttributes() throws ReflectiveOperationException
	{
		var field2Value = VALUE2;
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field1").getLastAttribute(), "value1");
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field2").getLastAttribute(), field2Value);
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field3").getLastAttribute(), 3);

		var result = (SingleConstructorMock) ConstructionStrategy.newInstance(SingleConstructorMock.class, attributes);

		Assertions.assertEquals("value1", result.getField1());
		Assertions.assertEquals(field2Value, result.getField2());
		Assertions.assertEquals(3, result.getField3());
	}

	@Test
	void shouldConstructCanonicalConstructorTypeFromPropertyMap() throws ReflectiveOperationException
	{
		var field2Value = VALUE2;
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field1").getLastAttribute(), "value1");
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field2").getLastAttribute(), field2Value);
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field3").getLastAttribute(), 3);

		var result = (SingleConstructorMock) ConstructionStrategy.newInstance(SingleConstructorMock.class,
				attributes);

		Assertions.assertEquals("value1", result.getField1());
		Assertions.assertEquals(field2Value, result.getField2());
		Assertions.assertEquals(3, result.getField3());
	}

	@Test
	void shouldPassNullForMissingCanonicalConstructorAttributes()
	{
		var field2Value = VALUE2;

		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field1").getLastAttribute(), "value1");
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field2").getLastAttribute(), field2Value);

		var result = (SingleConstructorMock) ConstructionStrategy.newInstance(SingleConstructorMock.class, attributes);

		Assertions.assertEquals("value1", result.getField1());
		Assertions.assertEquals(field2Value, result.getField2());
		Assertions.assertNull(result.getField3());
	}

	@Test
	void shouldPassNullForMissingCanonicalConstructorProperties()
	{
		var field2Value = VALUE2;
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field1").getLastAttribute(), "value1");
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field2").getLastAttribute(), field2Value);

		var result = (SingleConstructorMock) ConstructionStrategy.newInstance(SingleConstructorMock.class,
				attributes);

		Assertions.assertEquals("value1", result.getField1());
		Assertions.assertEquals(field2Value, result.getField2());
		Assertions.assertNull(result.getField3());
	}

	@Test
	void shouldFailWhenMissingPrimitiveConstructorAttributes()
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(PrimitiveConstructorMock.class, "field1").getLastAttribute(), "value1");

		var exception = Assertions.assertThrows(ConstructionException.class,
				() -> ConstructionStrategy.newInstance(PrimitiveConstructorMock.class, attributes));

		Assertions.assertTrue(exception.getMessage().contains("missing value for primitive parameter"));
		Assertions.assertTrue(exception.getMessage().contains("field1"));
		Assertions.assertTrue(exception.getMessage().contains("int"));
	}

	@Test
	void shouldUseCanonicalConstructorWhenRemainingAttributesAreBeanProperties() throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(CanonicalWithBeanAttributeMock.class, "name").getLastAttribute(), "Ana",
				Property.getProperty(CanonicalWithBeanAttributeMock.class, "description").getLastAttribute(), "Root");

		var result = (CanonicalWithBeanAttributeMock)
				ConstructionStrategy.newInstance(CanonicalWithBeanAttributeMock.class, attributes);

		Assertions.assertEquals("Ana", result.getName());
		Assertions.assertEquals("Root", result.getDescription());
	}

	static class CanonicalWithBeanAttributeMock
	{
		private final String name;
		private String description;

		@Canonical
		public CanonicalWithBeanAttributeMock(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
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
}
