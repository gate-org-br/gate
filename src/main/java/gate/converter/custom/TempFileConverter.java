package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.TempFile;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Part;

public class TempFileConverter implements Converter
{

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Arquivo temporário";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object.toString()) : "";
	}

	@Override
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		if (part == null)
			return null;

		return TempFile.of(part);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException, ConversionException
	{
		try (InputStream inputStream = rs.getBinaryStream(index))
		{
			return TempFile.of(inputStream);
		} catch (IOException ex)
		{
			throw new ConversionException("Error when trying to read temporary file from data base", ex);
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		try (InputStream inputStream = rs.getBinaryStream(fields))
		{
			return TempFile.of(inputStream);
		} catch (IOException ex)
		{
			throw new ConversionException("Error when trying to read temporary file from data base", ex);
		}
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value == null)
			ps.setNull(index, Types.VARBINARY);
		else
			ps.setBinaryStream(index, ((TempFile) value).getInputStream());
		return ++index;
	}

	@Override
	public String toString(Class<?> type, Object object
	)
	{
		throw new UnsupportedOperationException("Temporary files can't be represented as a string.");
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		throw new UnsupportedOperationException("Temporary files can't be represented as string.");
	}
}
