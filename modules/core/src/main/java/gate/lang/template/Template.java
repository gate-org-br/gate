package gate.lang.template;

import gate.error.EvaluableException;
import gate.error.NoSuchPropertyError;
import gate.error.TemplateException;
import gate.error.TemplatePropertyException;
import gate.lang.expression.Parameters;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used to generate documents from GTL templates
 */
public class Template
{

	private final List<Evaluable> evaluables;

	Template(List<Evaluable> evaluables)
	{
		this.evaluables = evaluables;
	}

	void evaluate(Writer document, List<Object> context, Parameters parameters) throws TemplateException
	{
		try
		{
			evaluables.forEach(evaluable -> evaluable.evaluate(document, context, parameters));
			document.flush();
		} catch (EvaluableException | IOException ex)
		{
			throw new TemplateException(ex.getMessage());
		} catch (NoSuchPropertyError ex)
		{
			throw new TemplatePropertyException(ex.getType(), ex.getProperty());
		}
	}

	/**
	 * Evaluates the template with the specified context.
	 *
	 * @param context  the context to be used for evaluation
	 * @param document the writer where to print the result document
	 * @throws TemplateException if an error occurs when evaluating the template or when printing the result document
	 */
	public void evaluate(Object context, Writer document) throws TemplateException
	{
		evaluate(document, new ArrayList<>(Collections.singletonList(context)), new Parameters());
	}

	/**
	 * Evaluates the template with the specified context.
	 *
	 * @param context    the context to be used for evaluation
	 * @param parameters parameters to be used for evaluation
	 * @param document   the writer where to print the result document
	 * @throws TemplateException if an error occurs when evaluating the template or when printing the result document
	 */
	public void evaluate(Object context, Parameters parameters, Writer document) throws TemplateException
	{
		evaluate(document, new ArrayList<>(Collections.singletonList(context)), new Parameters(parameters));
	}

	/**
	 * Evaluates the template with the specified context.
	 *
	 * @param context  the context to be used for evaluation
	 * @param document the file where to print the evaluated document
	 * @throws TemplateException if an error occurs when evaluating or saving the template
	 */
	public void evaluate(Object context, File document) throws TemplateException
	{
		document.getParentFile().mkdirs();
		try (FileWriter writer = new FileWriter(document))
		{
			evaluate(writer, new ArrayList<>(Collections.singletonList(context)), new Parameters());
		} catch (IOException ex)
		{
			throw new TemplateException(ex, ex.getMessage());
		}
	}

	/**
	 * Evaluates the template with the specified context.
	 *
	 * @param context    the context to be used for evaluation
	 * @param parameters parameters to be used for evaluation
	 * @param document   the file where to print the evaluated document
	 * @throws TemplateException if an error occurs when evaluating or saving the template
	 */
	public void evaluate(Object context, Parameters parameters, File document) throws TemplateException
	{
		document.getParentFile().mkdirs();
		try (FileWriter writer = new FileWriter(document))
		{
			evaluate(writer, new ArrayList<>(Collections.singletonList(context)), new Parameters(parameters));
		} catch (IOException ex)
		{
			throw new TemplateException(ex, ex.getMessage());
		}
	}

	/**
	 * Evaluates the template with the specified context.
	 *
	 * @param context the context to be used for evaluation
	 * @return the evaluated document string
	 * @throws TemplateException if an error occurs when evaluating the template
	 */
	public String evaluate(Object context) throws TemplateException
	{
		try (StringWriter writer = new StringWriter())
		{
			evaluate(writer, new ArrayList<>(Collections.singletonList(context)), new Parameters());
			writer.flush();
			return writer.toString();
		} catch (IOException ex)
		{
			throw new TemplateException(ex, ex.getMessage());
		}
	}

	/**
	 * Evaluates the template with the specified context.
	 *
	 * @param context    the context to be used for evaluation
	 * @param parameters parameters to be used for evaluation
	 * @return the evaluated document string
	 * @throws TemplateException if an error occurs when evaluating the template
	 */
	public String evaluate(Object context, Parameters parameters) throws TemplateException
	{
		try (StringWriter writer = new StringWriter())
		{
			evaluate(writer, new ArrayList<>(Collections.singletonList(context)), new Parameters(parameters));
			writer.flush();
			return writer.toString();
		} catch (IOException ex)
		{
			throw new TemplateException(ex, ex.getMessage());
		}
	}

	@Override
	public String toString()
	{
		return String.format("Template: %s", evaluables.toString());
	}

	/**
	 * Compiles the given string into a Template object.
	 *
	 * @param template the reader where to get the template to be compiled
	 * @return the compiled Template object
	 * @throws TemplateException if the given template is invalid
	 */
	public static Template compile(Reader template) throws TemplateException
	{
		return new TemplateParser().parse(template);
	}

	/**
	 * Compiles the given string into a Template object.
	 *
	 * @param template the string to be compiled
	 * @return the compiled Template object
	 * @throws TemplateException if the given template is invalid
	 */
	public static Template compile(String template) throws TemplateException
	{
		try (StringReader reader = new StringReader(template))
		{
			return compile(reader);
		}
	}

	/**
	 * Compiles the given template data into a Template object.
	 *
	 * @param template the template data to be compiled
	 * @return the compiled Template object
	 * @throws TemplateException if the given template is invalid
	 */
	public static Template compile(byte[] template) throws TemplateException
	{
		try (InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(template)))
		{
			return compile(reader);
		} catch (IOException e)
		{
			throw new TemplateException(String.format("Error trying to access template file %s.", e.getMessage()));
		}
	}

	/**
	 * Compiles the given template data into a Template object.
	 *
	 * @param template the template data to be compiled
	 * @param charset  the character set to be used when reading template data
	 * @return the compiled Template object
	 * @throws TemplateException if the given template is invalid
	 */
	public static Template compile(byte[] template, String charset) throws TemplateException
	{
		try (InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(template), charset))
		{
			return compile(reader);
		} catch (IOException e)
		{
			throw new TemplateException(String.format("Error trying to access template file %s.", e.getMessage()));
		}
	}

	/**
	 * Compiles the given URL resource into a Template object.
	 *
	 * @param template the URL resource to be compiled
	 * @return the compiled Template object
	 * @throws TemplateException if the given template is invalid or an error occurs when reading the resource data
	 */
	public static Template compile(URL template) throws TemplateException
	{
		try (InputStreamReader reader = new InputStreamReader(template.openStream()))
		{
			return compile(reader);
		} catch (IOException e)
		{
			throw new TemplateException(String.format("Error trying to access template file %s.", e.getMessage()));
		}
	}

	/**
	 * Compiles the given URL resource into a Template object.
	 *
	 * @param template the URL resource to be compiled
	 * @param charset  the character set to be used when reading the resource data
	 * @return the compiled Template object
	 * @throws TemplateException if the given template is invalid or an error occurs when reading the resource data
	 */
	public static Template compile(URL template, String charset) throws TemplateException
	{
		try (InputStreamReader reader = new InputStreamReader(template.openStream(), charset))
		{
			return compile(reader);
		} catch (IOException e)
		{
			throw new TemplateException(String.format("Error trying to access template file %s.", e.getMessage()));
		}
	}

	/**
	 * Compiles the given file into a Template object.
	 *
	 * @param template the file to be compiled
	 * @return the compiled Template object
	 * @throws TemplateException if the given template is invalid or an error occurs when reading the file data
	 */
	public static Template compile(File template) throws TemplateException
	{
		try (Reader reader = new FileReader(template))
		{
			return compile(reader);
		} catch (IOException e)
		{
			throw new TemplateException(String.format("Error trying to access template file %s.", e.getMessage()));
		}
	}

	/**
	 * Compiles the given file into a Template object.
	 *
	 * @param template the file to be compiled
	 * @param charset  the character set to be used when reading the file data
	 * @return the compiled Template object
	 * @throws TemplateException if the given template is invalid, or an error occurs when reading the file data
	 */
	public static Template compile(File template, String charset) throws TemplateException
	{
		try (Reader reader = new FileReader(template, Charset.forName(charset)))
		{
			return compile(reader);
		} catch (IOException e)
		{
			throw new TemplateException(String.format("Error trying to access template file %s.", e.getMessage()));
		}
	}
}