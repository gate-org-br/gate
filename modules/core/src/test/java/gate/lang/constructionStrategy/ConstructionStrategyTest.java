package gate.lang.constructionStrategy;

import gate.annotation.Canonical;
import gate.error.ConstructionException;
import gate.error.ConversionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;
import gate.lang.property.Property;
import mock.ContactMock;
import mock.UserMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static mock.ConstructionMocks.*;

class ConstructionStrategyTest
{

	private static final LocalDate VALUE2 = LocalDate.of(2, 2, 2);

	@BeforeEach
	void clearCache()
	{
		Cache.INSTANCE.clear();
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
	void shouldConstructCollectionTypeFromObjectFactory() throws ReflectiveOperationException
	{
		var attributes = new LinkedHashMap<Attribute, Object>();

		var result = ConstructionStrategy.newInstance(List.class, attributes);

		Assertions.assertInstanceOf(ArrayList.class, result);
	}

	@Test
	void shouldFailWhenInterfaceTypeIsNotSupportedByObjectFactory()
	{
		var attributes = new LinkedHashMap<Attribute, Object>();

		var exception = Assertions.assertThrows(ConstructionException.class,
				() -> ConstructionStrategy.newInstance(Runnable.class, attributes));

		Assertions.assertTrue(exception.getMessage().contains(Runnable.class.getName()));
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

		var exception = Assertions.assertThrows(ConstructionException.class,
				() -> ConstructionStrategy.newInstance(PrimitiveFactoryMock.class, attributes));

		Assertions.assertTrue(exception.getMessage().contains("missing value for primitive parameter"));
		Assertions.assertTrue(exception.getMessage().contains("field1"));
		Assertions.assertTrue(exception.getMessage().contains("int"));
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
	void shouldPreferExactConstructorOverCanonicalConstructor() throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(ExactOverCanonicalMock.class, "name").getLastAttribute(), "Ana");

		var result = (ExactOverCanonicalMock)
				ConstructionStrategy.newInstance(ExactOverCanonicalMock.class, attributes);

		Assertions.assertEquals("Ana", result.getName());
		Assertions.assertEquals("exact", result.getSource());
		Assertions.assertNull(result.getDescription());
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

	@Test
	void shouldIgnoreSealedSubtypeConstructorAlreadyCoveredByParent() throws ReflectiveOperationException
	{
		var parent = new SealedParentMock();
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(SealedParentMock.class, "parent").getLastAttribute(), parent,
				Property.getProperty(SealedChildMock.class, "name").getLastAttribute(), "Ana");

		var result = (SealedParentMock) Assertions.assertDoesNotThrow(
				() -> ConstructionStrategy.newInstance(SealedParentMock.class, attributes));

		Assertions.assertInstanceOf(SealedChildMock.class, result);
		Assertions.assertSame(parent, result.getParent());
		Assertions.assertEquals("Ana", ((SealedChildMock) result).getName());
	}

	@Test
	void shouldMatchSealedConstructorParameterAssignableFromSubtypeAttribute() throws ReflectiveOperationException
	{
		var parent = new SealedChildMock(null, "Parent");
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(SealedSubtypeAttributeMock.class, "parent").getLastAttribute(), parent,
				Property.getProperty(SealedChildMock.class, "name").getLastAttribute(), "Ana");

		var result = (SealedParentMock) Assertions.assertDoesNotThrow(
				() -> ConstructionStrategy.newInstance(SealedParentMock.class, attributes));

		Assertions.assertInstanceOf(SealedChildMock.class, result);
		Assertions.assertSame(parent, result.getParent());
		Assertions.assertEquals("Ana", ((SealedChildMock) result).getName());
	}

	@Test
	void shouldFailWhenSealedSubtypeConstructorsHaveSameSignatureNotCoveredByParent()
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(DuplicateSealedChildMock.class, "name").getLastAttribute(), "Ana");

		Assertions.assertThrows(ConstructionException.class,
				() -> ConstructionStrategy.newInstance(DuplicateSealedParentMock.class, attributes));
	}

	@Test
	void shouldDistinguishSealedSubtypeConstructorsWithSameParameterNameAndDifferentTypes()
			throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(StringTypedSealedChildMock.class, "value").getLastAttribute(), "Ana");

		var result = (TypedSealedParentMock) Assertions.assertDoesNotThrow(
				() -> ConstructionStrategy.newInstance(TypedSealedParentMock.class, attributes));

		Assertions.assertInstanceOf(StringTypedSealedChildMock.class, result);
		Assertions.assertEquals("Ana", ((StringTypedSealedChildMock) result).getValue());
	}

	@Test
	void shouldSelectMostSpecificSealedConstructorParameter() throws ReflectiveOperationException
	{
		var parent = new SpecificSealedChildMock(null);
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(SpecificSealedAttributeMock.class, "parent").getLastAttribute(), parent);

		var result = (SpecificSealedParentMock) Assertions.assertDoesNotThrow(
				() -> ConstructionStrategy.newInstance(SpecificSealedParentMock.class, attributes));

		Assertions.assertInstanceOf(SpecificSealedChildMock.class, result);
		Assertions.assertSame(parent, result.getParent());
	}

	@Test
	void shouldFailWhenSealedConstructorSpecificityIsCrossed()
	{
		var value = new CrossSealedSpecificMock();
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(CrossSealedAttributeMock.class, "parent").getLastAttribute(), value,
				Property.getProperty(CrossSealedAttributeMock.class, "owner").getLastAttribute(), value);

		Assertions.assertThrows(ConstructionException.class,
				() -> ConstructionStrategy.newInstance(CrossSealedParentMock.class, attributes));
	}

	@Test
	void shouldUseCanonicalSealedConstructorWhenItContainsAllAttributes() throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(CanonicalSealedChildMock.class, "name").getLastAttribute(), "Ana");

		var result = (CanonicalSealedParentMock) Assertions.assertDoesNotThrow(
				() -> ConstructionStrategy.newInstance(CanonicalSealedParentMock.class, attributes));

		Assertions.assertInstanceOf(CanonicalSealedChildMock.class, result);
		Assertions.assertEquals("Ana", ((CanonicalSealedChildMock) result).getName());
		Assertions.assertNull(((CanonicalSealedChildMock) result).getDescription());
	}

	static class SealedSubtypeAttributeMock
	{
		private SealedChildMock parent;

		public SealedChildMock getParent()
		{
			return parent;
		}
	}

	static class ExactOverCanonicalMock
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

	static sealed class DuplicateSealedParentMock permits DuplicateSealedChildMock, DuplicateSealedSiblingMock
	{
	}

	static final class DuplicateSealedChildMock extends DuplicateSealedParentMock
	{
		private final String name;

		public DuplicateSealedChildMock(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}

	static final class DuplicateSealedSiblingMock extends DuplicateSealedParentMock
	{
		public DuplicateSealedSiblingMock(String name)
		{
		}
	}

	static sealed class TypedSealedParentMock permits StringTypedSealedChildMock, IntegerTypedSealedChildMock
	{
	}

	static final class StringTypedSealedChildMock extends TypedSealedParentMock
	{
		private final String value;

		public StringTypedSealedChildMock(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	static final class IntegerTypedSealedChildMock extends TypedSealedParentMock
	{
		public IntegerTypedSealedChildMock(Integer value)
		{
		}
	}

	static class SpecificSealedAttributeMock
	{
		private SpecificSealedChildMock parent;

		public SpecificSealedChildMock getParent()
		{
			return parent;
		}
	}

	static sealed class SpecificSealedParentMock permits GeneralSealedChildMock, SpecificSealedChildMock
	{
		public SpecificSealedParentMock getParent()
		{
			return null;
		}
	}

	static final class GeneralSealedChildMock extends SpecificSealedParentMock
	{
		private final SpecificSealedParentMock parent;

		public GeneralSealedChildMock(SpecificSealedParentMock parent)
		{
			this.parent = parent;
		}

		@Override
		public SpecificSealedParentMock getParent()
		{
			return parent;
		}
	}

	static final class SpecificSealedChildMock extends SpecificSealedParentMock
	{
		private final SpecificSealedChildMock parent;

		public SpecificSealedChildMock(SpecificSealedChildMock parent)
		{
			this.parent = parent;
		}

		@Override
		public SpecificSealedParentMock getParent()
		{
			return parent;
		}
	}

	static class CrossSealedAttributeMock
	{
		private CrossSealedSpecificMock parent;
		private CrossSealedSpecificMock owner;

		public CrossSealedSpecificMock getParent()
		{
			return parent;
		}

		public CrossSealedSpecificMock getOwner()
		{
			return owner;
		}
	}

	static sealed class CrossSealedParentMock
			permits CrossSealedLeftMock, CrossSealedRightMock, CrossSealedSpecificMock
	{
	}

	static final class CrossSealedLeftMock extends CrossSealedParentMock
	{
		public CrossSealedLeftMock(CrossSealedSpecificMock parent, CrossSealedParentMock owner)
		{
		}
	}

	static final class CrossSealedRightMock extends CrossSealedParentMock
	{
		public CrossSealedRightMock(CrossSealedParentMock parent, CrossSealedSpecificMock owner)
		{
		}
	}

	static final class CrossSealedSpecificMock extends CrossSealedParentMock
	{
	}

	static sealed class CanonicalSealedParentMock permits CanonicalSealedChildMock, CanonicalSealedSiblingMock
	{
	}

	static final class CanonicalSealedChildMock extends CanonicalSealedParentMock
	{
		private final String name;
		private final String description;

		@Canonical
		public CanonicalSealedChildMock(String name, String description)
		{
			this.name = name;
			this.description = description;
		}

		public String getName()
		{
			return name;
		}

		public String getDescription()
		{
			return description;
		}
	}

	static final class CanonicalSealedSiblingMock extends CanonicalSealedParentMock
	{
		public CanonicalSealedSiblingMock(Integer code)
		{
		}
	}

	static sealed class SealedParentMock permits SealedChildMock, SealedSiblingMock
	{
		private final SealedParentMock parent;

		public SealedParentMock()
		{
			this(null);
		}

		public SealedParentMock(SealedParentMock parent)
		{
			this.parent = parent;
		}

		public SealedParentMock getParent()
		{
			return parent;
		}
	}

	static final class SealedChildMock extends SealedParentMock
	{
		private final String name;

		public SealedChildMock(SealedParentMock parent, String name)
		{
			super(parent);
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}

	static final class SealedSiblingMock extends SealedParentMock
	{
		public SealedSiblingMock(SealedParentMock parent)
		{
			super(parent);
		}
	}

}
