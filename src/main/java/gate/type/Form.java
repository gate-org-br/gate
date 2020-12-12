package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.FormConverter;
import gate.error.AppException;
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

	public void pack(int limit)
	{
		int i = 0;
		while (i < fields.size())
		{
			if (i + 8 < fields.size()
				&& fields.get(i).getMinSize() <= limit
				&& fields.get(i + 1).getMinSize() <= limit
				&& fields.get(i + 2).getMinSize() <= limit
				&& fields.get(i + 3).getMinSize() <= limit
				&& fields.get(i + 4).getMinSize() <= limit
				&& fields.get(i + 5).getMinSize() <= limit
				&& fields.get(i + 6).getMinSize() <= limit
				&& fields.get(i + 7).getMinSize() <= limit)
			{
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);

			} else if (i + 6 < fields.size()
				&& fields.get(i).getMinSize() <= limit
				&& fields.get(i + 1).getMinSize() <= limit
				&& fields.get(i + 2).getMinSize() <= limit
				&& fields.get(i + 3).getMinSize() <= limit
				&& fields.get(i + 4).getMinSize() <= limit * 2
				&& fields.get(i + 5).getMinSize() <= limit * 2)
			{
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.TWO);
				fields.get(i++).setSize(Field.Size.TWO);
			} else if (i + 6 < fields.size()
				&& fields.get(i).getMinSize() <= limit * 2
				&& fields.get(i + 1).getMinSize() <= limit * 2
				&& fields.get(i + 2).getMinSize() <= limit
				&& fields.get(i + 3).getMinSize() <= limit
				&& fields.get(i + 4).getMinSize() <= limit
				&& fields.get(i + 5).getMinSize() <= limit)
			{
				fields.get(i++).setSize(Field.Size.TWO);
				fields.get(i++).setSize(Field.Size.TWO);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
			} else if (i + 5 < fields.size()
				&& fields.get(i).getMinSize() <= limit * 4
				&& fields.get(i + 1).getMinSize() <= limit
				&& fields.get(i + 2).getMinSize() <= limit
				&& fields.get(i + 3).getMinSize() <= limit
				&& fields.get(i + 4).getMinSize() <= limit)
			{
				fields.get(i++).setSize(Field.Size.FOUR);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
			} else if (i + 5 < fields.size()
				&& fields.get(i).getMinSize() <= limit
				&& fields.get(i + 1).getMinSize() <= limit
				&& fields.get(i + 2).getMinSize() <= limit
				&& fields.get(i + 3).getMinSize() <= limit
				&& fields.get(i + 4).getMinSize() <= limit * 4)
			{
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.ONE);
				fields.get(i++).setSize(Field.Size.FOUR);
			} else if (i + 4 < fields.size()
				&& fields.get(i).getMinSize() <= limit * 2
				&& fields.get(i + 1).getMinSize() <= limit * 2
				&& fields.get(i + 2).getMinSize() <= limit * 2
				&& fields.get(i + 3).getMinSize() <= limit * 2)
			{
				fields.get(i++).setSize(Field.Size.TWO);
				fields.get(i++).setSize(Field.Size.TWO);
				fields.get(i++).setSize(Field.Size.TWO);
				fields.get(i++).setSize(Field.Size.TWO);
			} else if (i + 3 < fields.size()
				&& fields.get(i).getMinSize() <= limit * 2
				&& fields.get(i + 1).getMinSize() <= limit * 2
				&& fields.get(i + 2).getMinSize() <= limit * 4)
			{
				fields.get(i++).setSize(Field.Size.TWO);
				fields.get(i++).setSize(Field.Size.TWO);
				fields.get(i++).setSize(Field.Size.FOUR);
			} else if (i + 3 < fields.size()
				&& fields.get(i).getMinSize() <= limit * 4
				&& fields.get(i + 1).getMinSize() <= limit * 2
				&& fields.get(i + 2).getMinSize() <= limit * 2)
			{
				fields.get(i++).setSize(Field.Size.FOUR);
				fields.get(i++).setSize(Field.Size.TWO);
				fields.get(i++).setSize(Field.Size.TWO);
			} else if (i + 2 < fields.size()
				&& fields.get(i + 1).getMinSize() <= limit * 4
				&& fields.get(i + 2).getMinSize() <= limit * 4)
			{
				fields.get(i++).setSize(Field.Size.FOUR);
				fields.get(i++).setSize(Field.Size.FOUR);
			} else
				fields.get(i++).setSize(Field.Size.EIGHT);

		}
	}
}
