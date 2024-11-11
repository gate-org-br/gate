package gate.type;

import gate.annotation.Converter;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Required;
import gate.converter.custom.FieldConverter;
import gate.error.AppException;
import gate.error.ConversionException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import gate.type.collections.StringList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Icon("2198")
@Converter(FieldConverter.class)
public class Field implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Name("ID")
	@Description("Defina o id do campo.")
	private String id;

	@Name("Nome")
	@Description("Defina o nome do campo.")
	private String name;

	@Required
	@Name("Tamanho")
	@Description("Defina o número de colunas a serem ocupadas pelo campo no formulário.")
	private Size size;

	@Required
	@Name("Multiplo")
	@Description("Defina se o campo admite múltiplas linhas (caso seja de preenchimento livre), ou múltiplas opções (caso possua lista predefinida de opções).")
	private boolean multiple;

	@Name("Opções")
	@Description("Defina as opções possíveis de respostas para o campo. Separe as opções por vírgula. Deixe em branco se o campo for de preenchimento livre.")
	private StringList options;

	@Name("Valor Padrão")
	@Description("Defina o valor padrão do campo.")
	private StringList value;

	@Required
	@Name("Requerido")
	@Description("Defina se o campo é requerido.")
	private boolean required;

	@Name("Máscara")
	@Description("Defina uma máscara para o campo.")
	private String mask;

	@Name("Descrição")
	@Description("Informações adicionais sobre o campo.")
	private String description;

	@Name("Padrão")
	@Description("Expressão regular a ser utilizada para validar o campo.")
	private Pattern pattern;

	@Name("Tamanho Máximo")
	@Description("Número de caracteres máximo permitido para o campo.")
	private Integer maxlength;

	@Name("Somente Leitura")
	@Description("Define se o campo é somente leitura.")
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
		return new JsonObject()
				.setString("id", id)
				.setString("name", name)
				.setString("mask", mask)
				.setInt("maxlength", maxlength)
				.setObject("size", Size.class, size)
				.setString("description", description)
				.setObject("pattern", Pattern.class, pattern)
				.setBoolean("readonly", readonly ? true : null)
				.setBoolean("multiple", multiple ? true : null)
				.setBoolean("required", required ? true : null)
				.set("value", value != null && !value.isEmpty() ? JsonArray.of(value) : null)
				.set("options",
						options != null && !options.isEmpty() ? JsonArray.of(options) : null);
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
		Field field = new Field()
				.setId(jsonObject.getString("id").orElse(null))
				.setName(jsonObject.getString("name").orElse(null))
				.setMask(jsonObject.getString("mask").orElse(null))
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
		else if (options instanceof JsonArray)
			field.setOptions(((JsonArray) options).stream().map(JsonElement::toString)
					.collect(Collectors.toCollection(StringList::new)));

		JsonElement value = jsonObject.get("value");
		if (value instanceof JsonString)
			field.setValue(new StringList(value.toString()));
		else if (value instanceof JsonArray)
			field.setValue(((JsonArray) value).stream().map(JsonElement::toString)
					.collect(Collectors.toCollection(StringList::new)));

		return field;
	}

	public void validate() throws AppException
	{
		if (Boolean.TRUE.equals(getRequired()) && getValue().isEmpty())
			throw new AppException(String.format("O campo %s é requerido", getName()));

		if (getMaxlength() != null
				&& getValue().stream().anyMatch(e -> e.length() > getMaxlength()))
			throw new AppException(
					String.format("O tamanho máximo do campo %s é %s", getName(), getMaxlength()));

		if (getPattern() != null)
		{
			if (getValue().stream().anyMatch(e -> !getPattern().matcher(e).matches()))
				throw new AppException(
						String.format("Formato inválido para o campo %s", getName()));

			if (!getOptions().isEmpty()
					&& getValue().stream().anyMatch(e -> !getPattern().matcher(e).matches()))
				throw new AppException(String.format(
						"%s is not a valid option para o campo campo %s", value, getName()));
		}
	}

	public enum Size
	{

		@Name("1")
		ONE, @Name("2")
		TWO, @Name("4")
		FOUR, @Name("8")
		EIGHT;

		@Override
		public String toString()
		{
			return Name.Extractor.extract(this).orElse(name());
		}

		public static Size parse(String string)
		{
			return string != null
					? switch (string.trim())
			{
				case "0" ->
					ONE;
				case "1" ->
					TWO;
				case "2" ->
					FOUR;
				case "3" ->
					EIGHT;
				default ->
					null;
			} : null;
		}
	}
}
