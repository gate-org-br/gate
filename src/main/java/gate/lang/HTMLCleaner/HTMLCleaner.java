package gate.lang.HTMLCleaner;

import gate.error.TemplateException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * Used to convert the HTML documents to plain text documents
 */
public class HTMLCleaner
{

	private final List<Evaluable> evaluables;

	HTMLCleaner(List<Evaluable> evaluables)
	{
		this.evaluables = evaluables;
	}

	private String evaluate() throws TemplateException
	{
		try (StringWriter writer = new StringWriter())
		{
			evaluables.forEach(evaluable -> evaluable.evaluate(writer));
			writer.flush();
			return writer.toString();
		} catch (IOException ex)
		{
			throw new TemplateException(ex, ex.getMessage());
		}
	}

	/**
	 * Convert the specified HTML document to a plain text document
	 *
	 * @param document the HTML document to be converted
	 * @return the same document in plain text format
	 */
	public static String cleanup(String document) throws TemplateException
	{
		try (StringReader reader = new StringReader(document))
		{
			return new Parser().parse(reader).evaluate();
		}
	}

}
