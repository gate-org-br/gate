package gate.lang.constructionStrategy;

import gate.lang.property.Attribute;
import gate.lang.property.Property;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

class RecordStrategyTest extends ConstructionStrategyTestSupport
{
	@Test
	void shouldConstructRecordFromAttributes() throws ReflectiveOperationException
	{
		record PointMock(int x, int y) {}

		var attributes = new LinkedHashMap<Attribute, Object>();

		attributes.put(Property.getProperty(PointMock.class, "x").getLastAttribute(), 10);
		attributes.put(Property.getProperty(PointMock.class, "y").getLastAttribute(), 20);

		var result = (PointMock) ConstructionStrategy.newInstance(PointMock.class, attributes);

		Assertions.assertEquals(new PointMock(10, 20), result);
	}
}
