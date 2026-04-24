package gate.lang.template;

import gate.error.EvaluableException;
import gate.error.NoSuchPropertyError;
import gate.error.TemplateException;
import gate.error.TemplatePropertyException;
import gate.lang.expression.Parameters;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

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
	 * @param context the context to be used for evaluation
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
	 * @param context the context to be used for evaluation
	 * @param parameters parameters to be used for evaluation
	 * @param document the writer where to print the result document
	 * @throws TemplateException if an error occurs when evaluating the template or when printing the result document
	 */
	public void evaluate(Object context, Parameters parameters, Writer document) throws TemplateException
	{
		evaluate(document, new ArrayList<>(Collections.singletonList(context)), new Parameters(parameters));
	}

	/**
	 * Evaluates the template with the specified context.
	 *
	 * @param context the context to be used for evaluation
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
	 * @param context the context to be used for evaluation
	 * @param parameters parameters to be used for evaluation
	 * @param document the file where to print the evaluated document
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
	 * @param context the context to be used for evaluation
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
	 * @param charset the character set to be used when reading template data
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
	 * @param charset the character set to be used when reading the resource data
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
	 * @param charset the character set to be used when reading the file data
	 * @return the compiled Template object
	 * @throws TemplateException if the given template is invalid or an error occurs when reading the file data
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

	/**
	 * @param context the context to be used for evaluation
	 * @param template the reader with the template data to be compiled
	 * @param document the writer where to print the result document
	 * @throws gate.error.TemplateException if the template is invalid or an error occurs white trying to read it from resource file
	 * @deprecated use {@link compile(URL)} and {@link evaluate(Object)} instead
	 */
	@Deprecated
	public static void evaluate(Object context, Reader template, Writer document) throws TemplateException
	{
		new TemplateParser().parse(template)
			.evaluate(document, new ArrayList<>(Collections.singletonList(context)),
				new Parameters());
	}

	/**
	 * @param context the context to be used for evaluation
	 * @param template the file with the template data to be compiled
	 * @param document the file where to print the result document
	 * @throws gate.error.TemplateException if the template is invalid or an error occurs white trying to read it from resource file
	 * @deprecated use {@link compile(URL)} and {@link evaluate(Object)} instead
	 */
	@Deprecated
	public static void evaluate(Object context, File template, File document) throws TemplateException
	{
		try (Reader reader = new FileReader(template))
		{
			try (Writer writer = new FileWriter(document))
			{
				evaluate(context, reader, writer);
				writer.flush();
			}
		} catch (IOException e)
		{
			throw new TemplateException(String.format("Error trying to access template file %s.", e.getMessage()));
		}
	}

	/**
	 * @param context the context to be used for evaluation
	 * @param string the template data to be compiled
	 * @return the result document text
	 * @throws gate.error.TemplateException if the template is invalid or an error occurs white trying to read it from resource file
	 * @deprecated use {@link compile(URL)} and {@link evaluate(Object)} instead
	 */
	@Deprecated
	public static String evaluate(Object context, String string) throws TemplateException
	{
		try (StringReader reader = new StringReader(string))
		{
			try (StringWriter writer = new StringWriter())
			{
				Template.evaluate(context, reader, writer);
				return writer.toString();
			}
		} catch (IOException ex)
		{
			throw new TemplateException("Error trying to evaluate template: " + ex.getMessage());
		}
	}

	/**
	 * @param context the context to be used for evaluation
	 * @param data the template data to be compiled
	 * @return the result document text
	 * @throws gate.error.TemplateException if the template is invalid or an error occurs white trying to read it from resource file
	 * @deprecated use {@link compile(URL)} and {@link evaluate(Object)} instead
	 */
	@Deprecated
	public static String evaluate(Object context, byte[] data) throws TemplateException
	{
		try (Reader reader = new InputStreamReader(new ByteArrayInputStream(data)))
		{
			try (StringWriter writer = new StringWriter())
			{
				Template.evaluate(context, reader, writer);
				return writer.toString();
			}
		} catch (IOException e)
		{
			throw new TemplateException("Error trying to evaluate template: " + e.getMessage());
		}
	}

	/**
	 * @param context the context to be used for evaluation
	 * @param resource the resource from where to get the template to be compiled
	 * @return the result document text
	 * @throws gate.error.TemplateException if the template is invalid or an error occurs white trying to read it from resource file
	 * @deprecated use {@link compile(URL)} and {@link evaluate(Object)} instead
	 */
	@Deprecated
	public static String evaluate(Object context, URL resource) throws TemplateException
	{
		try (Reader reader = new InputStreamReader(resource.openStream()))
		{
			try (StringWriter writer = new StringWriter())
			{
				Template.evaluate(context, reader, writer);
				return writer.toString();
			}
		} catch (IOException e)
		{
			throw new TemplateException("Error trying to evaluate template: " + e.getMessage());
		}
	}

	/**
	 * @param context the context to be used for evaluation
	 * @param resource the resource from where to get the template to be compiled
	 * @param charset the character set to be used when reading the template resource
	 * @return the result document text
	 * @throws gate.error.TemplateException if the template is invalid or an error occurs white trying to read it from resource file
	 * @deprecated use {@link compile(URL)} and {@link evaluate(Object)} instead
	 */
	@Deprecated
	public static String evaluate(Object context, URL resource, String charset) throws TemplateException
	{
		try (Reader reader = new InputStreamReader(resource.openStream(), charset))
		{
			try (StringWriter writer = new StringWriter())
			{
				Template.evaluate(context, reader, writer);
				return writer.toString();
			}
		} catch (IOException e)
		{
			throw new TemplateException("Error trying to evaluate template: " + e.getMessage());
		}
	}
}
