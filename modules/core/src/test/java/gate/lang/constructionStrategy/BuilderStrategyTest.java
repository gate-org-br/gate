package gate.lang.constructionStrategy;

import gate.lang.property.Attribute;
import gate.lang.property.Property;
import mock.ConstructionMocks.BuilderMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

class BuilderStrategyTest extends ConstructionStrategyTestSupport
{
	@Test
	void shouldConstructBuilderTypeFromAttributes() throws ReflectiveOperationException
	{
		var field2Value = VALUE2;
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(BuilderMock.class, "field1").getLastAttribute(), "value1");
		attributes.put(Property.getProperty(BuilderMock.class, "field2").getLastAttribute(), field2Value);
		attributes.put(Property.getProperty(BuilderMock.class, "field3").getLastAttribute(), 3);

		var result = (BuilderMock) ConstructionStrategy.newInstance(BuilderMock.class, attributes);

		Assertions.assertEquals("value1", result.getField1());
		Assertions.assertEquals(field2Value, result.getField2());
		Assertions.assertEquals(3, result.getField3());
	}
}
