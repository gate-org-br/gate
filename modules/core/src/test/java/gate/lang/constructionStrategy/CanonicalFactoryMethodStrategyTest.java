package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.lang.property.Attribute;
import gate.lang.property.Property;
import mock.ConstructionMocks.PrimitiveFactoryMock;
import mock.ConstructionMocks.SingleConstructorMock;
import mock.ConstructionMocks.SingleFactoryMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

class CanonicalFactoryMethodStrategyTest extends ConstructionStrategyTestSupport
{
	@Test
	void shouldPassNullForMissingFactoryMethodAttributes() throws ReflectiveOperationException
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field1").getLastAttribute(), "value1");

		var result = (SingleFactoryMock) ConstructionStrategy.newInstance(SingleFactoryMock.class, attributes);

		Assertions.assertEquals("value1", result.getField1());
		Assertions.assertNull(result.getField2());
		Assertions.assertNull(result.getField3());
	}

	@Test
	void shouldPassNullForMissingFactoryMethodProperties()
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field1").getLastAttribute(), "value1");

		var result = (SingleFactoryMock) ConstructionStrategy
				.newInstance(SingleFactoryMock.class, attributes);

		Assertions.assertEquals("value1", result.getField1());
		Assertions.assertNull(result.getField2());
		Assertions.assertNull(result.getField3());
	}

	@Test
	void shouldFailWhenMissingPrimitiveFactoryMethodAttributes()
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(PrimitiveFactoryMock.class, "field1").getLastAttribute(), "value1");

		var exception = Assertions.assertThrows(ConstructionException.class,
				() -> ConstructionStrategy.newInstance(PrimitiveFactoryMock.class, attributes));

		Assertions.assertTrue(exception.getMessage().contains("missing value for primitive parameter"));
		Assertions.assertTrue(exception.getMessage().contains("field1"));
		Assertions.assertTrue(exception.getMessage().contains("int"));
	}
}
