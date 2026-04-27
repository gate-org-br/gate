package gate.converter;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.lang.csv.CSVFormatter;
import gate.lang.csv.CSVParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

@Description("CSV Data")
public class StringMatrixConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
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
			return reader.stream().map(e -> e.toArray(new String[0])).toArray(String[][]::new);
		}
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return toString(type, object);
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return String.format(format, toString(type, object));
	}

}
