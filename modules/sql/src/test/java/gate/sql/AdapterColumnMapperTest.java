package gate.sql;

import gate.adapter.columnMapper.ColumnMapper;
import gate.adapter.registrar.AdapterRegistrar;
import gate.annotation.Adapter;
import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class AdapterColumnMapperTest
{
	@Test
	public void testTypeAdapterProvidesColumnMapper()
	{
		assertInstanceOf(TypeAdapter.class, ColumnMapper.getColumnMapper(SampleType.class));
	}

	@Test
	public void testRegisteredAdapterProvidesColumnMapper()
	{
		assertInstanceOf(RegisteredTypeAdapter.class, ColumnMapper.getColumnMapper(RegisteredType.class));
	}

	@Adapter(TypeAdapter.class)
	private record SampleType(String value)
	{
	}

	private record RegisteredType(String value)
	{
	}

	public static class TypeAdapter implements ColumnMapper
	{
		@Override
		public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException, ConversionException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
		{
			throw new UnsupportedOperationException();
		}
	}

	public static class RegisteredTypeAdapter implements ColumnMapper
	{
		@Override
		public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException, ConversionException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
		{
			throw new UnsupportedOperationException();
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