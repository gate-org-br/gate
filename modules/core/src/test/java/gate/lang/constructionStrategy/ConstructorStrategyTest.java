package gate.lang.constructionStrategy;

import gate.lang.property.Attribute;
import gate.lang.property.Property;
import mock.ConstructionMocks.MultipleConstructorMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

class ConstructorStrategyTest extends ConstructionStrategyTestSupport
{
	@Test
	void shouldIgnoreConstructorThatDoesNotConsumeAllAttributes() throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(MultipleConstructorMock.class, "field1").getLastAttribute(),
				"Ana",
				Property.getProperty(MultipleConstructorMock.class, "field2").getLastAttribute(),
				LocalDate.of(2042, 1, 1));

		var result = (MultipleConstructorMock) Assertions.assertDoesNotThrow(
				() -> ConstructionStrategy.newInstance(MultipleConstructorMock.class, attributes));

		Assertions.assertEquals("Ana", result.getField1());
		Assertions.assertEquals(LocalDate.of(2042, 1, 1), result.getField2());
		Assertions.assertNull(result.getField3());
	}
}
