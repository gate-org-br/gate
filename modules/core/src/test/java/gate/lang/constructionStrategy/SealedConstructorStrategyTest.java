package gate.lang.constructionStrategy;

import gate.annotation.Canonical;
import gate.error.ConstructionException;
import gate.lang.property.Attribute;
import gate.lang.property.Property;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class SealedConstructorStrategyTest extends ConstructionStrategyTestSupport
{
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
	void shouldFailWhenSealedAttributeOwnerIsOutsideHierarchy() throws ReflectiveOperationException
	{
		var parent = new SealedChildMock(null, "Parent");
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(SealedSubtypeAttributeMock.class, "parent").getLastAttribute(), parent,
				Property.getProperty(SealedChildMock.class, "name").getLastAttribute(), "Ana");

		Assertions.assertThrows(ConstructionException.class,
				() -> ConstructionStrategy.newInstance(SealedParentMock.class, attributes));
	}

	@Test
	void shouldUseSealedAttributeOwnerEvenWhenSubtypeConstructorsHaveSameSignature()
			throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(DuplicateSealedChildMock.class, "name").getLastAttribute(), "Ana");

		var result = (DuplicateSealedParentMock) Assertions.assertDoesNotThrow(
				() -> ConstructionStrategy.newInstance(DuplicateSealedParentMock.class, attributes));

		Assertions.assertInstanceOf(DuplicateSealedChildMock.class, result);
		Assertions.assertEquals("Ana", ((DuplicateSealedChildMock) result).getName());
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
	void shouldFailWhenSealedConstructorsDifferOnlyBySpecificity() throws ReflectiveOperationException
	{
		var parent = new SpecificSealedChildMock(null);
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(SpecificSealedAttributeMock.class, "parent").getLastAttribute(), parent);

		Assertions.assertThrows(ConstructionException.class,
				() -> ConstructionStrategy.newInstance(SpecificSealedParentMock.class, attributes));
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

	@Test
	void shouldUseSingleCompatibleSealedConstructor()
			throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(SupersetSealedChildMock.class, "name").getLastAttribute(), "Ana");

		var result = (SupersetSealedParentMock) Assertions.assertDoesNotThrow(
				() -> ConstructionStrategy.newInstance(SupersetSealedParentMock.class, attributes));

		Assertions.assertInstanceOf(SupersetSealedChildMock.class, result);
		Assertions.assertEquals("Ana", ((SupersetSealedChildMock) result).getName());
	}

	@Test
	void shouldPreferCanonicalSealedConstructorOverExact()
			throws ReflectiveOperationException
	{
		var attributes = Map.<Attribute, Object>of(
				Property.getProperty(ExactOverCanonicalSealedChildMock.class, "name").getLastAttribute(), "Ana");

		var result = (ExactOverCanonicalSealedParentMock) ConstructionStrategy
				.newInstance(ExactOverCanonicalSealedParentMock.class, attributes);

		Assertions.assertInstanceOf(ExactOverCanonicalSealedChildMock.class, result);
		Assertions.assertEquals("canonical", ((ExactOverCanonicalSealedChildMock) result).getSource());
	}

	static class SealedSubtypeAttributeMock
	{
		private SealedChildMock parent;

		public SealedChildMock getParent()
		{
			return parent;
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

	static sealed class SupersetSealedParentMock permits SupersetSealedChildMock
	{
	}

	static final class SupersetSealedChildMock extends SupersetSealedParentMock
	{
		private String name;

		public SupersetSealedChildMock(String name, String description)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}

	static sealed class ExactOverCanonicalSealedParentMock
			permits ExactOverCanonicalSealedChildMock
	{
	}

	static final class ExactOverCanonicalSealedChildMock
			extends ExactOverCanonicalSealedParentMock
	{
		private final String name;
		private final String source;

		public ExactOverCanonicalSealedChildMock(String name)
		{
			this.name = name;
			source = "exact";
		}

		@Canonical
		public ExactOverCanonicalSealedChildMock(String name, String description)
		{
			this.name = name;
			source = "canonical";
		}

		public String getName()
		{
			return name;
		}

		public String getSource()
		{
			return source;
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
