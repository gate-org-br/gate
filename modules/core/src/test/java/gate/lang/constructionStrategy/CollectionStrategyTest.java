package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.lang.property.Attribute;
import gate.lang.property.Property;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class CollectionStrategyTest extends ConstructionStrategyTestSupport
{
	@Test
	void shouldConstructCollectionTypeFromObjectFactory() throws ReflectiveOperationException
	{
		var attributes = new LinkedHashMap<Attribute, Object>();

		var result = ConstructionStrategy.newInstance(List.class, attributes);

		Assertions.assertInstanceOf(ArrayList.class, result);
	}

	@Test
	void shouldConstructAndPopulateCollectionTypeFromProperties() throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(List.class, "[]").getLastAttribute(), List.of("A", "B"));

		var result = ConstructionStrategy.newInstance(List.class, attributes);

		Assertions.assertInstanceOf(ArrayList.class, result);
		Assertions.assertEquals(List.of("A", "B"), result);
	}

	@Test
	void shouldFailWhenInterfaceTypeIsNotSupportedByObjectFactory()
	{
		var attributes = new LinkedHashMap<Attribute, Object>();

		var exception = Assertions.assertThrows(ConstructionException.class,
				() -> ConstructionStrategy.newInstance(Runnable.class, attributes));

		Assertions.assertTrue(exception.getMessage().contains(Runnable.class.getName()));
	}
}
