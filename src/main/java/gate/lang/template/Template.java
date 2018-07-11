package gate.lang.template;

import gate.error.EvaluableException;
import gate.error.TemplateException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Template implements Evaluable
{

	private final List<Evaluable> evaluables;

	public Template(List<Evaluable> evaluables)
	{
		this.evaluables = evaluables;
	}

	@Override
	public void evaluate(List<Object> context, Map<String, Object> parameters, Writer writer) throws TemplateException
	{
		try
		{
			for (Evaluable evaluable : evaluables)
				evaluable.evaluate(context, parameters, writer);
		} catch (EvaluableException ex)
		{
			throw new TemplateException(ex.getMessage(), ex);
		}
	}

	@Override
	public String toString()
	{
		return String.format("Template: %s", evaluables.toString());
	}

	public static void evaluate(Object context, Reader template, Writer document) throws TemplateException
	{
		new TemplateParser().parse(template)
				.evaluate(new ArrayList<>(Arrays.asList(context)),
						new HashMap<>(), document);
	}

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
			throw new TemplateException("Error trying to access template file %s.", e.getMessage());
		}
	}

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
			throw new TemplateException("Error trying to evaluate template.", ex.getMessage());
		}
	}

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
			throw new TemplateException("Error trying to evaluate template.", e.getMessage());
		}
	}
}
