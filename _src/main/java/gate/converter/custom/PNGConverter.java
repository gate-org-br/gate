package gate.converter.custom;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.converter.Converter;
import gate.type.PNG;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Part;

public class PNGConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return null;
	}

	@Override
	public Object ofString(Class<?> type, String string)
	{
		return new PNG(string);
	}

	@Override
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		if (part == null
			|| part.getSubmittedFileName().isEmpty())
			return null;

		try
		{
			try (InputStream is = part.getInputStream())
			{
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
				{
					for (int c = is.read(); c != -1; c = is.read())
						baos.write(c);
					return new PNG(baos.toByteArray());
				}
			}
		} catch (IOException ex)
		{
			throw new ConversionException("Erro ao obter arquivo", ex);
		}

	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type)
		throws SQLException
	{
		byte[] bytes = rs.getBytes(fields);
		if (rs.wasNull())
			return null;
		return new PNG(bytes);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		byte[] bytes = rs.getBytes(fields);
		if (rs.wasNull())
			return null;
		return new PNG(bytes);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setBytes(fields++, ((PNG) value).getBytes());
		else
			ps.setNull(fields++, Types.BINARY);
		return fields;
	}
}
