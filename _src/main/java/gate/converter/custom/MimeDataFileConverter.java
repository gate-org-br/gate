package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.io.ByteArrayReader;
import gate.type.mime.MimeDataFile;
import gate.type.mime.MimeText;
import gate.util.Strings;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Part;

public class MimeDataFileConverter implements Converter
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
		throws ConversionException
	{
		return !Strings.empty(string)
			? MimeDataFile.parse(string) : null;
	}

	@Override
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		if (part == null)
			return null;

		try
		{
			try (InputStream is = part.getInputStream())
			{
				byte[] bytes = ByteArrayReader.getInstance().read(is);
				String[] contentType = part.getContentType().split("/");

				if (bytes.length != 0)
					if (contentType.length != 2)
						return new MimeDataFile(bytes, part.getSubmittedFileName());
					else
						return new MimeDataFile(contentType[0], contentType[1], bytes, part.getSubmittedFileName());
				else
					return null;
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
		return object != null ? ((MimeText) object).getText() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, ((MimeText) object).getText()) : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type)
		throws SQLException, ConversionException
	{
		String data = rs.getString(fields);
		return !rs.wasNull()
			? MimeDataFile.parse(data)
			: null;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type)
		throws SQLException, ConversionException
	{
		String data = rs.getString(fields);
		return !rs.wasNull()
			? MimeDataFile.parse(data)
			: null;
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value)
		throws SQLException
	{
		if (value != null)
			ps.setString(fields++, value.toString());
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}
