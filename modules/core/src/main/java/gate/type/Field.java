package gate.type;

import java.io.Serial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Required;
import gate.error.AppException;
import gate.error.ConversionException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import gate.type.collections.StringList;

@Icon("2198")
public class Field implements Serializable
{


	@Serial
	private static final long serialVersionUID = 1L;

	@Name
	@Description
	private String id;

	@Name
	@Description
	private String name;

	@Name
	@Description
	private Size size;

	@Required
	@Name
	@Description
	private boolean multiple;

	@Name
	@Description
	private StringList options;

	@Name
	@Description
	private StringList value;

	@Required
	@Name
	@Description
	private boolean required;

	@Name
	@Description
	private String mask;

	@Name
	@Description
	private String description;

	@Name
	@Description
	private Pattern pattern;

	@Name
	@Description
	private Integer maxlength;

	@Name
	@Description
	private boolean readonly;

	public String getId()
	{
		return id;
	}

	public Field setId(String id)
	{
		this.id = id;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Field setName(String name)
	{
		this.name = name;
		return this;
	}

	public StringList getValue()
	{
		if (value == null)
			value = new StringList();
		return value;
	}

	public Field setValue(StringList value)
	{
		this.value = value;
		return this;
	}

	public String getMask()
	{
		return mask;
	}

	public Field setMask(String mask)
	{
		this.mask = mask;
		return this;
	}

	public Size getSize()
	{
		return size;
	}

	public Field setSize(Size size)
	{
		this.size = size;
		return this;
	}

	public boolean getMultiple()
	{
		return multiple;
	}

	public Field setMultiple(boolean multiple)
	{
		this.multiple = multiple;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public Field setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new ArrayList<>();
		if (Boolean.TRUE.equals(getRequired()))
			constraints.add(new Required.Implementation("required"));
		if (getMaxlength() != null)
			constraints.add(new Maxlength.Implementation(getMaxlength()));
		if (getPattern() != null)
			constraints.add(new gate.constraint.Pattern.Implementation(getPattern()));
		return constraints;
	}

	public StringList getOptions()
	{
		if (options == null)
			options = new StringList();
		return options;
	}

	public Field setOptions(StringList options)
	{
		this.options = options;
		return this;
	}

	public Boolean getRequired()
	{
		return required;
	}

	public Field setRequired(boolean required)
	{
		this.required = required;
		return this;
	}

	public Pattern getPattern()
	{
		return pattern;
	}

	public Field setPattern(Pattern pattern)
	{
		this.pattern = pattern;
		return this;
	}

	public Integer getMaxlength()
	{
		return maxlength;
	}

	public Field setMaxlength(Integer maxLength)
	{
		this.maxlength = maxLength;
		return this;
	}

	public boolean getReadonly()
	{
		return readonly;
	}

	public Field setReadonly(boolean readonly)
	{
		this.readonly = readonly;
		return this;
	}

	public int getMinSize()
	{
		return Math.max(value != null ? value.stream().mapToInt(e -> e.length()).sum() : 0,
				name != null ? name.length() : 0);
	}

	public JsonObject toJson()
	{
		return new JsonObject().setString("id", id).setString("name", name).setString("mask", mask)
				.setInt("maxlength", maxlength).setObject("size", Size.class, size)
				.setString("description", description).setObject("pattern", Pattern.class, pattern)
				.setBoolean("readonly", readonly ? true : null).setBoolean("multiple", multiple ? true : null)
				.setBoolean("required", required ? true : null)
				.set("value", value != null && !value.isEmpty() ? JsonArray.wrap(value) : null)
				.set("options", options != null && !options.isEmpty() ? JsonArray.wrap(options) : null);
	}

	@Override
	public String toString()
	{
		return toJson().toString();
	}

	public static Field parse(String string) throws ConversionException
	{
		return parse(JsonObject.parse(string));
	}

	public static Field parse(JsonObject jsonObject) throws ConversionException
	{
		Field field = new Field().setId(jsonObject.getString("id").orElse(null))
				.setName(jsonObject.getString("name").orElse(null)).setMask(jsonObject.getString("mask").orElse(null))
				.setMaxlength(jsonObject.getInt("maxlength").orElse(null))
				.setDescription(jsonObject.getString("description").orElse(null))
				.setReadonly(jsonObject.getBoolean("readonly").orElse(Boolean.FALSE))
				.setMultiple(jsonObject.getBoolean("multiple").orElse(Boolean.FALSE))
				.setRequired(jsonObject.getBoolean("required").orElse(Boolean.FALSE))
				.setSize(jsonObject.getString("size").map(Size::parse).orElse(null))
				.setPattern(jsonObject.getObject("pattern", Pattern.class).orElse(null));

		JsonElement options = jsonObject.get("options");
		if (options instanceof JsonString)
			field.setOptions(new StringList(options.toString()));
		else if (options instanceof JsonArray jsonArray)
			field.setOptions(
					jsonArray.stream().map(JsonElement::toString).collect(Collectors.toCollection(StringList::new)));

		JsonElement value = jsonObject.get("value");
		if (value instanceof JsonString)
			field.setValue(new StringList(value.toString()));
		else if (value instanceof JsonArray jsonArray)
			field.setValue(
					jsonArray.stream().map(JsonElement::toString).collect(Collectors.toCollection(StringList::new)));

		return field;
	}

	public void validate() throws AppException
	{
		if (Boolean.TRUE.equals(getRequired()) && getValue().isEmpty())
			throw new AppException(String.format("O campo %s é requerido", getName()));

		if (getMaxlength() != null && getValue().stream().anyMatch(e -> e.length() > getMaxlength()))
			throw new AppException(String.format("O tamanho máximo do campo %s é %s", getName(), getMaxlength()));

		if (getPattern() != null)
		{
			if (getValue().stream().anyMatch(e -> !getPattern().matcher(e).matches()))
				throw new AppException(String.format("Formato inválido para o campo %s", getName()));

			if (!getOptions().isEmpty() && getValue().stream().anyMatch(e -> !getPattern().matcher(e).matches()))
				throw new AppException(
						String.format("%s is not a valid option para o campo campo %s", value, getName()));
		}
	}

	public enum Size
	{

		@Name
		ONE, @Name
	TWO, @Name
	FOUR, @Name
	EIGHT;

		@Override
		public String toString()
		{
			return Name.Extractor.extract(this).orElse(name());
		}

		public static Size parse(String string)
		{
			if (string == null)
				return null;

			return switch (string.trim())
			{
				case "0" -> ONE;
				case "1" -> TWO;
				case "2" -> FOUR;
				case "3" -> EIGHT;
				default -> null;
			};
		}
	}
}