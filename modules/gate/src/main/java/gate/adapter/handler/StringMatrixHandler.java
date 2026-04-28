package gate.adapter.handler;

import gate.error.ConversionException;
import gate.lang.csv.CSVParser;
import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StringMatrixHandler implements Handler
{
	@Override
	public void handle(jakarta.servlet.http.HttpServletRequest request,
	                   jakarta.servlet.http.HttpServletResponse response, Object value)
	{
		throw new UnsupportedOperationException("String matrices can't be handled as a servlet response.");
	}

	@Override
	@SuppressWarnings("resource")
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		try (CSVParser reader =
				CSVParser.of(new BufferedReader(new InputStreamReader(part.getInputStream()))))
		{
			return reader.stream().map(e -> e.toArray(new String[0])).toArray(String[][]::new);
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		}
	}
}
