package gate.type;

import gate.error.AppException;
import gate.type.collections.StringList;
import gate.annotation.Converter;
import gate.converter.custom.FieldConverter;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Required;
import gate.error.ConversionException;
import gate.lang.json.JsonObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Icon("2198")
@Converter(FieldConverter.class)
public class Field implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Required
	@Name("Nome")
	@Description("Defina com o texto do campo.")
	private String name;

	@Required
	@Name("Tamanho")
	@Description("Defina o número de colunas a serem ocupadas pelo campo no formulário.")
	private Size size;

	@Required
	@Name("Multiplo")
	@Description("Defina se o campo admite múltiplas linhas (caso seja de preenchimento livre), ou múltiplas opções (caso possua lista predefinida de opções).")
	private Boolean multiple;

	@Name("Opções")
	@Description("Defina as opções possíveis de respostas para o campo. Separe as opções por vírgula. Deixe em branco se o campo for de preenchimento livre.")
	private StringList options;

	@Name("Valor Padrão")
	@Description("Defina o valor padrão do campo.")
	private StringList value;

	@Required
	@Name("Requerido")
	@Description("Defina se o campo é requerido.")
	private Boolean required;

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
	private Boolean readonly;

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
		if (size == null)
			size = Size.ONE;
		return size;
	}

	public Field setSize(Size size)
	{
		this.size = size;
		return this;
	}

	public Boolean getMultiple()
	{
		if (multiple == null)
			multiple = false;
		return multiple;
	}

	public Field setMultiple(Boolean multiple)
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

	public List<Constraint.Implementation> getConstraints()
	{
		List<Constraint.Implementation> constraints = new ArrayList<>();
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
		if (required == null)
			required = false;
		return required;
	}

	public Field setRequired(Boolean required)
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

	public Boolean getReadonly()
	{
		return readonly;
	}

	public Field setReadonly(Boolean readonly)
	{
		this.readonly = readonly;
		return this;
	}

	@Override
	public String toString()
	{
		return new JsonObject()
			.setString("name", name)
			.setObject("size", Size.class, size)
			.setBoolean("multiple", multiple)
			.setObject("options", StringList.class, options)
			.setObject("value", StringList.class, value)
			.setBoolean("required", required)
			.setString("mask", mask)
			.setString("description", description)
			.setObject("pattern", Pattern.class, pattern)
			.setInt("maxlength", maxlength)
			.setBoolean("readonly", readonly)
			.toString();
	}

	public static Field parse(String string) throws ConversionException
	{
		return parse(JsonObject.parse(string));
	}

	public static Field parse(JsonObject jsonObject) throws ConversionException
	{
		return new Field()
			.setName(jsonObject.getString("name").orElse(null))
			.setSize(jsonObject.getObject("size", Size.class).orElse(Size.EIGHT))
			.setReadonly(jsonObject.getBoolean("readonly").orElse(Boolean.FALSE))
			.setMultiple(jsonObject.getBoolean("multiple").orElse(Boolean.FALSE))
			.setRequired(jsonObject.getBoolean("required").orElse(Boolean.FALSE))
			.setOptions(jsonObject.getObject("options", StringList.class).orElse(null))
			.setValue(jsonObject.getObject("value", StringList.class).orElse(null))
			.setMask(jsonObject.getString("mask").orElse(null))
			.setDescription(jsonObject.getString("description").orElse(null))
			.setPattern(jsonObject.getObject("pattern", Pattern.class).orElse(null))
			.setMaxlength(jsonObject.getInt("maxlength").orElse(null));
	}

	public void validate() throws AppException
	{
		if (Boolean.TRUE.equals(getRequired()) && getValue().isEmpty())
			throw new AppException(String.format("O campo %s é requerido", getName()));

		if (getMaxlength() != null
			&& getValue().stream().anyMatch(e -> e.length() > getMaxlength()))
			throw new AppException(String.format("O tamanho máximo do campo %s é %s",
				getName(), getMaxlength()));

		if (getPattern() != null
			&& getValue().stream().anyMatch(e -> !getPattern().matcher(e).matches()))
			throw new AppException(String.format("Formato inválido para o campo %s", getName()));

		if (!getOptions().isEmpty()
			&& getValue().stream().anyMatch(e -> !getPattern().matcher(e).matches()))
			throw new AppException(String.format("%s is not a valid option para o campo campo %s", value, getName()));
	}

	public enum Size
	{

		ONE("1", new BigDecimal("12.5")),
		TWO("2", new BigDecimal("25.0")),
		THREE("3", new BigDecimal("37.5")),
		FOUR("4", new BigDecimal("50.0")),
		FIVE("5", new BigDecimal("62.5")),
		SIX("6", new BigDecimal("75.0")),
		SEVEN("7", new BigDecimal("87.5")),
		EIGHT("8", new BigDecimal("100"));

		private final String string;
		private final BigDecimal percentage;

		Size(String string, BigDecimal percentage)
		{
			this.string = string;
			this.percentage = percentage;
		}

		@Override
		public String toString()
		{
			return string;
		}

		public BigDecimal getPercentage()
		{
			return percentage;
		}

		public static Size parse(String string) throws ParseException
		{
			switch (string.trim())
			{
				case "1":
					return ONE;
				case "2":
					return TWO;
				case "3":
					return THREE;
				case "4":
					return FOUR;
				case "5":
					return FIVE;
				case "6":
					return SIX;
				case "7":
					return SEVEN;
				case "8":
					return EIGHT;
				default:
					throw new ParseException(string + " não é um número de colunas válido", 0);
			}
		}
	}
}
