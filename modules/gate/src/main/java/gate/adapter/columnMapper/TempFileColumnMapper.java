package gate.adapter.columnMapper;

import gate.type.TempFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class TempFileColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type)
			throws SQLException
	{
		try (InputStream inputStream = rs.getBinaryStream(index))
		{
			return inputStream != null ? TempFile.of(inputStream) : null;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type)
			throws SQLException
	{
		try (InputStream inputStream = rs.getBinaryStream(fields))
		{
			return inputStream != null ? TempFile.of(inputStream) : null;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value)
			throws SQLException
	{
		if (value != null)
		{
			TempFile tempFile = (TempFile) value;
			try
			{
				ps.setBytes(index++, tempFile.getBytes());
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		} else
			ps.setNull(index++, Types.BINARY);
		return index;
	}

	@Override
	public int writeToPreparedStatement(ColumnWriter writer, int index, Object value)
			throws SQLException
	{
		PreparedStatement ps = writer.getPreparedStatement();
		if (value instanceof TempFile tempFile)
		{
			try
			{
				InputStream inputStream = tempFile.getInputStream();
				try
				{
					ps.setBinaryStream(index++, inputStream, tempFile.length());
					writer.onClose(() ->
					{
						try
						{
							inputStream.close();
						} catch (IOException ex)
						{
							throw new UncheckedIOException(ex);
						}
					});
				} catch (SQLException | RuntimeException ex)
				{
					inputStream.close();
					throw ex;
				}
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		} else
			ps.setNull(index++, Types.BINARY);
		return index;
	}
}