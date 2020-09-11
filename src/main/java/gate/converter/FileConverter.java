package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Part;

public class FileConverter implements Converter
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

			File file = File.createTempFile("File", "." + part.getSubmittedFileName());
			try (InputStream inputStream = part.getInputStream();
				OutputStream outputStream = new FileOutputStream(file))
			{
				inputStream.transferTo(outputStream);
				return file;
			}
		} catch (IOException ex)
		{
			throw new ConversionException(ex, "Error trying to convert uploaded file");
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException, ConversionException
	{
		try
		{
			File file = File.createTempFile("File", "tmp");
			try (InputStream inputStream = rs.getBinaryStream(index);
				OutputStream outputStream = new FileOutputStream(file))
			{
				inputStream.transferTo(outputStream);
				return file;
			}
		} catch (IOException ex)
		{
			throw new ConversionException("Error when trying to read file from data base", ex);
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		try
		{
			File file = File.createTempFile("File", "tmp");
			try (InputStream inputStream = rs.getBinaryStream(fields);
				OutputStream outputStream = new FileOutputStream(file))
			{
				inputStream.transferTo(outputStream);
				return file;
			}
		} catch (IOException ex)
		{
			throw new ConversionException("Error when trying to read file from data base", ex);
		}
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
		{
			try (InputStream inputStream = new FileInputStream((File) value);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
			{
				inputStream.transferTo(outputStream);
				ps.setBytes(index, outputStream.toByteArray());
			} catch (IOException ex)
			{
				throw new SQLException("Error when trying to write file to data base", ex);
			}
		} else
			ps.setNull(index, Types.VARBINARY);
		return ++index;
	}

	@Override
	public String toString(Class<?> type, Object object
	)
	{
		throw new UnsupportedOperationException("Files can't be represented as strings.");
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		throw new UnsupportedOperationException("Files can't be represented as strings.");
	}
}
