package gate.adapter.columnMapper;

import gate.type.TempFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NamedTempFileColumnMapper extends TempFileColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type)
			throws SQLException
	{
		try (InputStream inputStream = rs.getBinaryStream(index))
		{
			if (inputStream == null)
				return null;
			TempFile tempFile = TempFile.of(inputStream);
			return tempFile.named(tempFile.getName());
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
			if (inputStream == null)
				return null;
			TempFile tempFile = TempFile.of(inputStream);
			return tempFile.named(tempFile.getName());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
