package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.type.DataFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Part;

public class DataFileConverter implements Converter
{

	private static final List<String> SUFIXES
		= Arrays.asList("name", "size", "data");

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
		throws ConversionException
	{
		try
		{
			if (string != null && string.trim().length() > 0)
			{
				byte[] bytes = Base64.getDecoder().decode(string);
				try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes)))
				{
					return ois.readObject();
				}
			}

			return null;
		} catch (IOException | ClassNotFoundException e)
		{
			throw new ConversionException(String.format(
				"%s não é um objeto válido.", string));
		}
	}

	@Override
	public Object ofPart(Class<?> type, Part part)
	{
		if (part == null
			|| part.getSubmittedFileName().isEmpty())
			return null;

		try (InputStream is = part.getInputStream())
		{
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				for (int c = is.read(); c != -1; c = is.read())
					baos.write(c);
				return new DataFile(baos.toByteArray(), part.getSubmittedFileName());
			}
		} catch (IOException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		try
		{
			if (object == null)
				return new String();
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				try (ObjectOutputStream ous = new ObjectOutputStream(baos))
				{
					ous.writeObject(object);
				}
				return Base64.getEncoder().encodeToString(baos.toByteArray());
			}

		} catch (IOException e)
		{
			throw new AppError(e);
		}
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
	public List<String> getSufixes()
	{
		return SUFIXES;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type)
		throws SQLException
	{
		String name = rs.getString(fields);
		if (rs.wasNull())
			return null;
		rs.getLong(fields + 1);
		if (rs.wasNull())
			return null;
		byte[] data = rs.getBytes(fields + 2);
		if (rs.wasNull())
			return null;
		return new DataFile(data, name);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type)
		throws SQLException
	{
		String name = rs.getString(fields + Converter.SEPARATOR + SUFIXES.get(0));
		if (rs.wasNull())
			return null;
		rs.getLong(fields + Converter.SEPARATOR + SUFIXES.get(1));
		if (rs.wasNull())
			return null;
		byte[] data = rs.getBytes(fields + Converter.SEPARATOR + SUFIXES.get(2));
		if (rs.wasNull())
			return null;
		return new DataFile(data, name);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
		{
			DataFile dataFile = (DataFile) value;
			ps.setString(fields++, dataFile.getName());
			ps.setLong(fields++, dataFile.getData().length);
			ps.setBytes(fields++, dataFile.getData());
		} else
		{
			ps.setNull(fields++, Types.VARCHAR);
			ps.setNull(fields++, Types.INTEGER);
			ps.setNull(fields++, Types.BINARY);
		}
		return fields;
	}
}
