package gate.type;

import gate.error.AppException;
import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.FormConverter;
import gate.error.ConversionException;
import gate.error.UncheckedConversionEception;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Icon("2044")
@Converter(FormConverter.class)
public class Form implements Serializable
{

	private static final long serialVersionUID = 1L;

	private List<Field> fields;

	public List<Field> getFields()
	{
		if (fields == null)
			fields = new ArrayList<>();
		return fields;
	}

	public Form setFields(List<Field> fields)
	{
		this.fields = fields;
		return this;
	}

	@Override
	public String toString()
	{
		return getFields().stream()
			.map(Field::toString)
			.collect(Collectors.joining(",", "[", "]"));
	}

	public static Form parse(String string) throws ConversionException
	{
		try
		{
			return new Form().setFields(JsonArray.parse(string)
				.stream()
				.map(e -> (JsonObject) e)
				.map(e -> UncheckedConversionEception.execute(() -> Field.parse(e)))
				.collect(Collectors.toList()));
		} catch (UncheckedConversionEception ex)
		{
			throw ex.getCause();
		}
	}

	public void validate() throws AppException
	{
		for (Field field : getFields())
			field.validate();
	}

	public static Map<String, Map<String, Long>>
		getStatistics(List<Form> forms)
	{
		return forms.stream()
			.flatMap(e -> e.getFields().stream())
			.filter(e -> !e.getOptions().isEmpty())
			.collect(Collectors.groupingBy(Field::getName,
				Collectors.collectingAndThen(Collectors.toList(),
					e -> e.stream().flatMap(v -> v.getValue().stream())
						.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())))));
	}
}
