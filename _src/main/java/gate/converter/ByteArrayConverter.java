package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Part;

public class ByteArrayConverter implements Converter
{

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "BLOB";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type)
			throws SQLException
	{
		return rs.getBytes(index);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields,
			Class<?> type) throws SQLException
	{
		return rs.getBytes(fields);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setBytes(index, (byte[]) value);
		else
			ps.setNull(index, Types.BLOB);
		return ++index;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		return Base64.getEncoder().encodeToString((byte[]) object);
	}

	@Override
	public Object ofString(Class<?> type, String string)
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;
		return Base64.getDecoder().decode(string);
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return toString(type, object);
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return String.format(format, toString(type, object));
	}

	@Override
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		try (BufferedInputStream stream = new BufferedInputStream(part.getInputStream());
				ByteArrayOutputStream bytes = new ByteArrayOutputStream())
		{
			for (int i = stream.read(); i != -1; i = stream.read())
				bytes.write(i);
			return bytes.toByteArray();
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		}
	}
}
