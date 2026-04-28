package gate.adapter.converter;

import gate.adapter.metadata.Metadata;
import gate.adapter.registrar.AdapterRegistrar;
import gate.annotation.Adapter;
import gate.constraint.Constraint;
import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class AdapterConverterTest
{
	@Test
	public void testTypeAdapterProvidesConverter()
	{
		assertInstanceOf(TypeAdapter.class, Converter.getConverter(SampleType.class));
		assertEquals("value", Converter.toString(new SampleType("value")));
	}

	@Test
	public void testTypeAdapterProvidesMetadata()
	{
		assertEquals("Adapter metadata", Metadata.getMetadata(SampleType.class).description());
	}

	@Test
	public void testRegisteredAdapterProvidesConverter()
	{
		assertInstanceOf(RegisteredTypeAdapter.class, Converter.getConverter(RegisteredType.class));
		assertEquals("registered", Converter.toString(new RegisteredType("registered")));
	}

	@Test
	public void testRegisteredAdapterProvidesMetadata()
	{
		assertEquals("Registered adapter metadata", Metadata.getMetadata(RegisteredType.class).description());
	}

	@Adapter(TypeAdapter.class)
	private record SampleType(String value)
	{
	}

	private record RegisteredType(String value)
	{
	}

	public static class TypeAdapter implements Converter, Metadata
	{
		@Override
		public List<Constraint.Implementation<?>> getConstraints()
		{
			return Collections.emptyList();
		}

		@Override
		public String toString(Class<?> type, Object object)
		{
			return ((SampleType) object).value();
		}

		@Override
		public Object ofString(Class<?> type, String string) throws ConversionException
		{
			return new SampleType(string);
		}

		@Override public String render(Class<?> type, Object object) {return toString(type, object);}

		@Override public String render(Class<?> type, Object object, String format) {return toString(type, object);}

		@Override
		public String description()
		{
			return "Adapter metadata";
		}
	}

	public static class RegisteredTypeAdapter implements Converter, Metadata
	{
		@Override
		public List<Constraint.Implementation<?>> getConstraints()
		{
			return Collections.emptyList();
		}

		@Override
		public String toString(Class<?> type, Object object)
		{
			return ((RegisteredType) object).value();
		}

		@Override
		public Object ofString(Class<?> type, String string) throws ConversionException
		{
			return new RegisteredType(string);
		}

		@Override public String render(Class<?> type, Object object) {return toString(type, object);}

		@Override public String render(Class<?> type, Object object, String format) {return toString(type, object);}

		@Override
		public String description()
		{
			return "Registered adapter metadata";
		}
	}

	public static class TestAdapterRegistrar implements AdapterRegistrar
	{
		@Override
		public Map<Class<?>, Object> entries()
		{
			return java.util.Map.of(RegisteredType.class, new RegisteredTypeAdapter());
		}
	}
}