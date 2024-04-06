package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class PathConverter implements Converter
{

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Arquivo";
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
		try
		{
			if (part == null)
				return null;

			String storage = CDI.current()
				.select(ServletContext.class)
				.get().getInitParameter("storage");

			File file = File.createTempFile("File",
				"." + part.getSubmittedFileName(),
				storage != null ? new File(storage) : null);

			try (InputStream inputStream = part.getInputStream();
				OutputStream outputStream = new FileOutputStream(file))
			{
				inputStream.transferTo(outputStream);
				return file.toPath();
			}
		} catch (IOException ex)
		{
			throw new ConversionException(ex, "Error trying to convert uploaded file");
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException, ConversionException
	{
		String string = rs.getString(index);
		if (string == null)
			return null;
		return Path.of(string);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		String string = rs.getString(fields);
		if (string == null)
			return null;
		return Path.of(string);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(index, value.toString());
		else
			ps.setNull(index, Types.VARCHAR);
		return ++index;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		return string != null && !string.isBlank() ? Path.of(string) : null;
	}
}
