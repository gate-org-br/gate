package gate.type;

import gate.annotation.Icon;
import gate.error.AppException;
import gate.error.ConversionException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Icon("2044")
public class Form implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	private List<Field> fields;

	public List<Field> getFields() {return fields == null ? fields = new ArrayList<>() : fields;}

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

	public static Form valueOf(String string) {return valueOf(JsonElement.parse(string));}

	public static Form valueOf(JsonArray json) throws ConversionException
	{
		return new Form().setFields(json.stream()
				.map(e -> (JsonObject) e)
				.map(Field::valueOf)
				.collect(Collectors.toList()));
	}

	public static Form valueOf(JsonObject json) throws ConversionException
	{
		return new Form().setFields(json
				.entrySet().stream()
				.map(e ->
				{
					Field field = Field.of(Field.Schema.builder()
							.name(e.getKey())
							.build());
					field.setValue(Field.Value.of(toList(String.valueOf(e.getValue().unwrap()))));
					return field;
				})
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

	public JsonArray toJson()
	{
		return getFields().stream().map(Field::toJson)
				.collect(Collectors.toCollection(JsonArray::new));
	}

	public void validate() throws AppException
	{
		for (Field field : getFields())
			field.validate();
	}

	public void validate(Form form) throws AppException
	{
		if (form == null)
			throw new AppException("Form is required");

		if (getFields().size() != form.getFields().size())
			throw new AppException("Form schema mismatch");

		for (int i = 0; i < getFields().size(); i++)
			getFields().get(i).validate(form.getFields().get(i));
	}

	public static Map<String, Map<String, Long>>
	getStatistics(List<Form> forms)
	{
		return forms.stream()
				.flatMap(e -> e.getFields().stream())
				.filter(e -> !e.schema().options().isEmpty())
				.collect(Collectors.groupingBy(e -> e.schema().name(),
						Collectors.collectingAndThen(Collectors.toList(),
								e -> e.stream().flatMap(v -> v.getValue().stream())
										.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())))));
	}

	public List<String> getValues(String id)
	{
		return getFields().stream()
				.filter(e -> id.equals(e.schema().id()))
				.findAny()
				.map(Field::getValue)
				.orElseGet(Field.Value::of);
	}

	private static List<String> toList(String string)
	{
		return Arrays.stream(string.split(",|;|\\r?\\n"))
				.map(String::trim)
				.filter(e -> !e.isEmpty())
				.collect(Collectors.toList());
	}

	public Optional<String> getValue(String id)
	{
		var value = getValues(id);
		if (value.isEmpty())
			return Optional.empty();
		return Optional.of(String.join("\n", value));
	}

	public Form add(Field field)
	{
		getFields().add(field);
		return this;
	}

	private void setSize(int index, Field.Schema.Size size)
	{
		Field field = fields.get(index);
		Field sized = Field.of(Field.Schema.builder(field.schema()).size(size).build());
		sized.setValue(field.getValue());
		fields.set(index, sized);
	}

	public void pack(int limit)
	{
		int i = 0;
		while (i < fields.size())
		{
			if (i + 8 < fields.size()
			    && fields.get(i).minSize() <= limit
			    && fields.get(i + 1).minSize() <= limit
			    && fields.get(i + 2).minSize() <= limit
			    && fields.get(i + 3).minSize() <= limit
			    && fields.get(i + 4).minSize() <= limit
			    && fields.get(i + 5).minSize() <= limit
			    && fields.get(i + 6).minSize() <= limit
			    && fields.get(i + 7).minSize() <= limit)
			{
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);

			} else if (i + 6 < fields.size()
			           && fields.get(i).minSize() <= limit
			           && fields.get(i + 1).minSize() <= limit
			           && fields.get(i + 2).minSize() <= limit
			           && fields.get(i + 3).minSize() <= limit
			           && fields.get(i + 4).minSize() <= limit * 2
			           && fields.get(i + 5).minSize() <= limit * 2)
			{
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.TWO);
				setSize(i++, Field.Schema.Size.TWO);
			} else if (i + 6 < fields.size()
			           && fields.get(i).minSize() <= limit * 2
			           && fields.get(i + 1).minSize() <= limit * 2
			           && fields.get(i + 2).minSize() <= limit
			           && fields.get(i + 3).minSize() <= limit
			           && fields.get(i + 4).minSize() <= limit
			           && fields.get(i + 5).minSize() <= limit)
			{
				setSize(i++, Field.Schema.Size.TWO);
				setSize(i++, Field.Schema.Size.TWO);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
			} else if (i + 5 < fields.size()
			           && fields.get(i).minSize() <= limit * 4
			           && fields.get(i + 1).minSize() <= limit
			           && fields.get(i + 2).minSize() <= limit
			           && fields.get(i + 3).minSize() <= limit
			           && fields.get(i + 4).minSize() <= limit)
			{
				setSize(i++, Field.Schema.Size.FOUR);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
			} else if (i + 5 < fields.size()
			           && fields.get(i).minSize() <= limit
			           && fields.get(i + 1).minSize() <= limit
			           && fields.get(i + 2).minSize() <= limit
			           && fields.get(i + 3).minSize() <= limit
			           && fields.get(i + 4).minSize() <= limit * 4)
			{
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.ONE);
				setSize(i++, Field.Schema.Size.FOUR);
			} else if (i + 4 < fields.size()
			           && fields.get(i).minSize() <= limit * 2
			           && fields.get(i + 1).minSize() <= limit * 2
			           && fields.get(i + 2).minSize() <= limit * 2
			           && fields.get(i + 3).minSize() <= limit * 2)
			{
				setSize(i++, Field.Schema.Size.TWO);
				setSize(i++, Field.Schema.Size.TWO);
				setSize(i++, Field.Schema.Size.TWO);
				setSize(i++, Field.Schema.Size.TWO);
			} else if (i + 3 < fields.size()
			           && fields.get(i).minSize() <= limit * 2
			           && fields.get(i + 1).minSize() <= limit * 2
			           && fields.get(i + 2).minSize() <= limit * 4)
			{
				setSize(i++, Field.Schema.Size.TWO);
				setSize(i++, Field.Schema.Size.TWO);
				setSize(i++, Field.Schema.Size.FOUR);
			} else if (i + 3 < fields.size()
			           && fields.get(i).minSize() <= limit * 4
			           && fields.get(i + 1).minSize() <= limit * 2
			           && fields.get(i + 2).minSize() <= limit * 2)
			{
				setSize(i++, Field.Schema.Size.FOUR);
				setSize(i++, Field.Schema.Size.TWO);
				setSize(i++, Field.Schema.Size.TWO);
			} else if (i + 2 < fields.size()
			           && fields.get(i + 1).minSize() <= limit * 4
			           && fields.get(i + 2).minSize() <= limit * 4)
			{
				setSize(i++, Field.Schema.Size.FOUR);
				setSize(i++, Field.Schema.Size.FOUR);
			} else
				setSize(i++, Field.Schema.Size.EIGHT);

		}
	}
}