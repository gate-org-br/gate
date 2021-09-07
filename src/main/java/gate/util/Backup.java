package gate.util;

import gate.converter.Converter;
import gate.error.ConversionException;
import gate.lang.csv.CSVFormatter;
import gate.lang.csv.CSVParser;
import gate.lang.csv.Row;
import gate.lang.property.Property;
import gate.type.DataFile;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Backup<T>
{

	private String name;
	private Class<T> type;
	private List<Property> properties;

	public Backup(String name, Class<T> type, List<Property> properties)
	{
		if (name == null)
			throw new IllegalArgumentException("name");
		if (type == null)
			throw new IllegalArgumentException("type");
		if (properties == null)
			throw new IllegalArgumentException("properties");
		this.type = type;
		this.name = name;
		this.properties = properties;
	}

	public Backup(String name, Class<T> type, String... properties)
	{
		this(name, type, Property.getProperties(type, properties));
	}

	public List<Property> getProperties()
	{
		return properties;
	}

	public void save(List<T> objs, Writer writer) throws IOException
	{
		try (CSVFormatter csv = CSVFormatter.of(writer))
		{
			objs.forEach(obj
				-> csv.writeLine(properties.stream().map(e -> e.getValue(obj))
					.map(Converter::toString).collect(Collectors.toList())));
		}
	}

	public DataFile save(List<T> objs) throws ConversionException
	{
		try (StringWriter string = new StringWriter())
		{
			save(objs, string);
			return new DataFile(string.toString().getBytes(Charset.forName("UTF-8")), String.format("%s.csv", name));
		} catch (IOException e)
		{
			throw new ConversionException("Erro ao gerar arquivo CSV");
		}
	}

	public void save(List<T> objs, String filename) throws IOException
	{
		save(objs, new FileWriter(filename));
	}

	public List<T> load(Reader reader) throws ConversionException
	{
		try (CSVParser CSV = CSVParser.of(new BufferedReader(reader)))
		{
			List<T> objs = new ArrayList<>();
			for (Optional<Row> optional = CSV.parseLine();
				optional.isPresent();
				optional = CSV.parseLine())
			{
				List<String> values = optional.get();
				if (!values.isEmpty())
				{
					T obj = type.getConstructor().newInstance();
					for (int i = 0; i < properties.size(); i++)
						if (values.size() > i)
						{
							String value = values.get(i).trim();
							Property property = properties.get(i);
							Class<?> clazz = property.getRawType();
							property.setValue(obj, Converter.getConverter(clazz).ofString(clazz, value));

						}
					objs.add(obj);
				}
			}
			return objs;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IOException
			| IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
		{
			throw new ConversionException("Erro ao interpretar CSV: " + ex.getMessage());
		}
	}

	public List<T> load(DataFile file) throws ConversionException
	{
		return load(new StringReader(new String(file.getData(), StandardCharsets.UTF_8)));
	}

	public List<T> load(String filename) throws ConversionException, FileNotFoundException
	{
		return load(new FileReader(filename));
	}
}
