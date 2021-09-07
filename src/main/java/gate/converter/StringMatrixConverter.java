package gate.converter;

import gate.constraint.Constraint;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.lang.csv.CSVFormatter;
import gate.lang.csv.CSVParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Part;

public class StringMatrixConverter implements Converter
{

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "CSV Data";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type)
		throws SQLException, ConversionException
	{
		return ofString(type, rs.getString(index));
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields,
		Class<?> type) throws SQLException, ConversionException
	{
		return ofString(type, rs.getString(fields));
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(index, toString(String[][].class, value));
		else
			ps.setNull(index, Types.VARCHAR);
		return ++index;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		try (StringWriter writer = new StringWriter();
			CSVFormatter formatter = CSVFormatter.of(writer))
		{
			for (String[] values : (String[][]) object)
				formatter.writeLine(values);
			writer.flush();
			return writer.toString();
		} catch (IOException ex)
		{
			throw new AppError(ex);
		}
	}

	@Override
	public Object ofString(Class<?> type, String string)
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		try (CSVParser reader = CSVParser.of(new BufferedReader(new StringReader(string))))
		{
			return reader.stream()
				.map(e -> e.toArray(new String[0]))
				.toArray(String[][]::new);
		}
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
		try (CSVParser reader = CSVParser.of(new BufferedReader(new InputStreamReader(part.getInputStream()))))
		{
			return reader.stream()
				.map(e -> e.toArray(new String[0]))
				.toArray(String[][]::new);
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		}
	}
}
