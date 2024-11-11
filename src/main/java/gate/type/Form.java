package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.annotation.Icon;
import gate.converter.custom.FormConverter;
import gate.error.AppException;
import gate.error.ConversionException;
import gate.handler.FormHandler;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.type.collections.StringList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Icon("2044")
@Handler(FormHandler.class)
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
		return toJson().toString();
	}

	public static Form valueOf(String string) throws ConversionException
	{
		return valueOf(JsonArray.parse(string));
	}

	public static Form valueOf(JsonArray json) throws ConversionException
	{
		return new Form().setFields(json
				.stream()
				.map(e -> (JsonObject) e)
				.map(Field::parse)
				.collect(Collectors.toList()));
	}

	public static Form valueOf(JsonObject json) throws ConversionException
	{
		return new Form().setFields(json
				.entrySet().stream()
				.map(e -> new Field()
				.setName(e.getKey())
				.setValue(new StringList(e.getValue().toString())))
				.collect(Collectors.toList()));
	}

	public static Form valueOf(JsonElement json) throws ConversionException
	{
		if (json instanceof JsonObject object)
			return Form.valueOf(object);
		if (json instanceof JsonArray array)
			return Form.valueOf(array);

		throw new ConversionException("Invalid json element");
	}

	public List<String> getValue(String id)
	{
		return getFields().stream()
				.filter(e -> id.equals(e.getId()))
				.findAny()
				.map(e -> e.getValue())
				.orElseGet(StringList::new);
	}

	public JsonArray toJson()
	{
		return getFields().stream().map(e -> e.toJson())
				.collect(Collectors.toCollection(JsonArray::new));
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