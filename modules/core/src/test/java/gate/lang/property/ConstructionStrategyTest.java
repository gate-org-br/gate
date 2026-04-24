package gate.lang.property;

import gate.error.ConversionException;
import gate.function.TriFunction;
import mock.ContactMock;
import mock.UserMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static mock.ConstructionMocks.*;

class ConstructionStrategyTest
{

	private static final LocalDate VALUE2 = LocalDate.of(2, 2, 2);

	@BeforeEach
	void clearCache()
	{
		ConstructionStrategy.CACHE.clear();
	}

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

	@Test
	void shouldReuseExistingBeanInstanceWhenUpdating() throws ReflectiveOperationException
	{
		var originalUser = new UserMock().setId(1).setName("Original");
		var contact = new ContactMock().setUser(originalUser);
		var user = Property.getProperty(ContactMock.class, "user").getLastAttribute();
		var properties = Map.<Attribute, Object>of(user, "ignored");

		var result = (ContactMock) ConstructionStrategy.newInstance(ContactMock.class, contact, properties,
				(attribute, currentValue, sourceValue) -> currentValue);

		Assertions.assertSame(contact, result);
		Assertions.assertSame(originalUser, result.getUser());
	}

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

		TriFunction<Attribute, Object, Object, Object> getValue = (attribute, currentValue, sourceValue) ->
				switch (attribute.toString())
				{
					case "field1" -> "value1";
					case "field2" -> field2Value;
					case "field3" -> 3;
					default -> sourceValue;
				};

		var result = (SingleConstructorMock) ConstructionStrategy.newInstance(SingleConstructorMock.class, null, attributes, getValue);

		Assertions.assertEquals("value1", result.getField1());
		Assertions.assertEquals(field2Value, result.getField2());
		Assertions.assertEquals(3, result.getField3());
	}

	@Test
	void shouldPassNullForMissingCanonicalConstructorAttributes() throws ReflectiveOperationException
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
	void shouldPassNullForMissingCanonicalConstructorProperties() throws ReflectiveOperationException
	{
		var field2Value = VALUE2;
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field1").getLastAttribute(), "value1");
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field2").getLastAttribute(), field2Value);

		var result = (SingleConstructorMock) ConstructionStrategy.newInstance(SingleConstructorMock.class, null, attributes,
				(attribute, currentValue, sourceValue) -> attribute.toString().equals("field1") ? "value1" : field2Value);

		Assertions.assertEquals("value1", result.getField1());
		Assertions.assertEquals(field2Value, result.getField2());
		Assertions.assertNull(result.getField3());
	}

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
	void shouldPassNullForMissingFactoryMethodProperties() throws ReflectiveOperationException
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(SingleConstructorMock.class, "field1").getLastAttribute(), "value1");

		var result = (SingleFactoryMock) ConstructionStrategy
				.newInstance(SingleFactoryMock.class, null, attributes,
						(attribute, currentValue, sourceValue) -> "value1");

		Assertions.assertEquals("value1", result.getField1());
		Assertions.assertNull(result.getField2());
		Assertions.assertNull(result.getField3());
	}

	@Test
	void shouldFailWhenMissingPrimitiveFactoryMethodAttributes()
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(PrimitiveFactoryMock.class, "field1").getLastAttribute(), "value1");

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> ConstructionStrategy.newInstance(PrimitiveFactoryMock.class, attributes));
	}

	@Test
	void shouldFailWhenMissingPrimitiveConstructorAttributes()
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(Property.getProperty(PrimitiveConstructorMock.class, "field1").getLastAttribute(), "value1");

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> ConstructionStrategy.newInstance(PrimitiveConstructorMock.class, attributes));
	}

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
	void shouldIgnoreConstructorThatDoesNotConsumeAllAttributes() throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(MultipleConstructorMock.class, "field1").getLastAttribute(), "Ana",
				Property.getProperty(MultipleConstructorMock.class, "field2").getLastAttribute(), LocalDate.of(2042, 1, 1));

		var result = (MultipleConstructorMock) Assertions.assertDoesNotThrow(
				() -> ConstructionStrategy.newInstance(MultipleConstructorMock.class, attributes));

		Assertions.assertEquals("Ana", result.getField1());
		Assertions.assertEquals(LocalDate.of(2042, 1, 1), result.getField2());
		Assertions.assertNull(result.getField3());
	}

}