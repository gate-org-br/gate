package gate.lang.property;

import gate.error.ConversionException;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

class ConstructionStrategyTest
{

	@BeforeEach
	void clearCache()
	{
		ConstructionStrategy.CACHE.clear();
	}

	@Test
	void shouldConstructRecordFromAttributes() throws ReflectiveOperationException
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(attribute("x", int.class), 10);
		attributes.put(attribute("y", int.class), 20);

		var result = (Point) ConstructionStrategy.newInstance(Point.class, attributes);

		Assertions.assertEquals(new Point(10, 20), result);
	}

	@Test
	void shouldConstructBuilderTypeFromAttributes() throws ReflectiveOperationException
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(attribute("name", String.class), "Ana");
		attributes.put(attribute("age", Integer.class), 33);

		var result = (BuilderValue) ConstructionStrategy.newInstance(BuilderValue.class, attributes);

		Assertions.assertEquals("Ana", result.getName());
		Assertions.assertEquals(33, result.getAge());
	}

	@Test
	void shouldReuseExistingBeanInstanceWhenUpdating() throws ReflectiveOperationException
	{
		var originalMarker = new Marker("original");
		var bean = new MutableBean().setMarker(originalMarker);
		var marker = attribute("marker", Marker.class, MutableBean::getMarker,
				(beanValue, markerValue) -> beanValue.setMarker((Marker) markerValue));
		var properties = Map.<Attribute, Object>of(marker, "ignored");

		var result = (MutableBean) ConstructionStrategy.newInstance(MutableBean.class, bean, properties,
				(attribute, currentValue, sourceValue) -> currentValue);

		Assertions.assertSame(bean, result);
		Assertions.assertSame(originalMarker, result.getMarker());
		Assertions.assertEquals(1, result.getSetterCalls());
	}

	@Test
	void shouldConstructCanonicalConstructorTypeFromAttributes() throws ReflectiveOperationException
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		attributes.put(attribute("arg0", String.class), "Ana");
		attributes.put(attribute("arg1", Integer.class), 42);

		var result = (CanonicalValue) ConstructionStrategy.newInstance(CanonicalValue.class, attributes);

		Assertions.assertEquals("Ana", result.getArg0());
		Assertions.assertEquals(42, result.getArg1());
	}

	@Test
	void shouldConstructCanonicalConstructorTypeFromPropertyMap() throws ReflectiveOperationException
	{
		var properties = Map.<Attribute, Object>of(
				attribute("arg0", String.class), "source-name",
				attribute("arg1", Integer.class), 1);

		TriFunction<Attribute, Object, Object, Object> getValue = (attribute, currentValue, sourceValue) ->
				attribute.toString().equals("arg0") ? "Computed" : 99;

		var result = (CanonicalValue) ConstructionStrategy.newInstance(CanonicalValue.class, null, properties, getValue);

		Assertions.assertEquals("Computed", result.getArg0());
		Assertions.assertEquals(99, result.getArg1());
	}

	@Test
	void shouldThrowWhenCanonicalConstructorSelectionIsAmbiguous()
	{
		var attributes = Map.<Attribute, Object>of(
				attribute("arg0", String.class), "Ana",
				attribute("arg1", Integer.class), 1);

		Assertions.assertThrows(ConversionException.class,
				() -> ConstructionStrategy.newInstance(AmbiguousCanonical.class, attributes));
	}

	record Point(int x, int y)
	{
	}

	static class BuilderValue
	{
		private final String name;
		private final Integer age;

		private BuilderValue(Builder builder)
		{
			this.name = builder.name;
			this.age = builder.age;
		}

		public static Builder builder()
		{
			return new Builder();
		}

		public String getName()
		{
			return name;
		}

		public Integer getAge()
		{
			return age;
		}

		static class Builder
		{
			private String name;
			private Integer age;

			public Builder name(String name)
			{
				this.name = name;
				return this;
			}

			public Builder age(Integer age)
			{
				this.age = age;
				return this;
			}

			public BuilderValue build()
			{
				return new BuilderValue(this);
			}
		}
	}

	static class MutableBean
	{
		private Marker marker;
		private int setterCalls;

		public Marker getMarker()
		{
			return marker;
		}

		public MutableBean setMarker(Marker marker)
		{
			this.setterCalls++;
			this.marker = marker;
			return this;
		}

		public int getSetterCalls()
		{
			return setterCalls;
		}
	}

	static class Marker
	{
		private final String value;

		Marker(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	static class CanonicalValue
	{
		private final String arg0;
		private final Integer arg1;

		public CanonicalValue(String arg0, Integer arg1)
		{
			this.arg0 = arg0;
			this.arg1 = arg1;
		}

		public String getArg0()
		{
			return arg0;
		}

		public Integer getArg1()
		{
			return arg1;
		}
	}

	static class AmbiguousCanonical
	{
		private final String arg0;
		private final Integer arg1;

		public AmbiguousCanonical(String arg0, Integer arg1)
		{
			this.arg0 = arg0;
			this.arg1 = arg1;
		}

		public AmbiguousCanonical(Object arg0, Number arg1)
		{
			this.arg0 = String.valueOf(arg0);
			this.arg1 = arg1 == null ? null : arg1.intValue();
		}

		public String getArg0()
		{
			return arg0;
		}

		public Integer getArg1()
		{
			return arg1;
		}
	}

	private static Attribute attribute(String name, Class<?> rawType)
	{
		return attribute(name, rawType, object -> null, (object, value) ->
		{
		});
	}

	private static <T> Attribute attribute(String name,
										   Class<?> rawType,
										   Function<T, Object> getter,
										   BiConsumer<T, Object> setter)
	{
		return new TestAttribute<>(name, rawType, getter, setter);
	}

	private record TestAttribute<T>(String name,
									Class<?> rawType,
									Function<T, Object> getter,
									BiConsumer<T, Object> setter) implements Attribute
	{
		@Override
		public java.lang.reflect.Type getGenericType()
		{
			return rawType;
		}

		@Override
		public java.lang.reflect.Type getElementType()
		{
			return null;
		}

		@Override
		public Class<?> getRawType()
		{
			return rawType;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object getValue(Object object)
		{
			return getter.apply((T) object);
		}

		@Override
		public void setValue(Object object, Object value)
		{
			setter.accept((T) object, value);
		}

		@Override
		public Object forceValue(Object object)
		{
			return getValue(object);
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}