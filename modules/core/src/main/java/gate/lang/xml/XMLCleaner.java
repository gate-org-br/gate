package gate.lang.xml;

import gate.error.TemplateException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * Used to convert the HTML documents to plain text documents
 */
public class XMLCleaner
{

	private final List<XMLEvaluable> evaluables;

	XMLCleaner(List<XMLEvaluable> evaluables)
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
	 * @return the same document in plain text format or the document itself if it is not a valid HTML document
	 */
	public static String cleanup(String document)
	{
		if (document == null)
			return null;

		try (StringReader reader = new StringReader(document))
		{
			return new XMLCleaner(new XMLParser().parse(reader)).evaluate();
		} catch (RuntimeException ex)
		{
			return document;
		}
	}

}
