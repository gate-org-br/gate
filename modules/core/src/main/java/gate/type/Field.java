package gate.type;

import gate.adapter.converter.CollectionConverter;
import gate.adapter.converter.Converter;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Required;
import gate.error.AppException;
import gate.lang.json.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Icon("2198")
public class Field implements Serializable
{
	@Serial
	private static final long serialVersionUID = 1L;

	@Name
	@Required
	@Description
	private final Schema schema;

	@Name
	@Description
	private Value value;

	public Schema schema() {return schema;}

	private Field(Schema schema) {this.schema = schema;}

	public static Field of(Schema schema) {return new Field(schema);}

	public static Field of(Schema.Builder schema) {return new Field(schema.build());}

	public Value getValue() {return value == null ? value = new Value() : value;}

	public Field setValue(Value value)
	{
		this.value = value;
		return this;
	}

	public int minSize()
	{
		return Math.max(value != null ? value.stream().mapToInt(String::length).sum() : 0,
				schema.name() != null ? schema.name().length() : 0);
	}

	public JsonObject toJson()
	{
		return new JsonObject()
				.set("schema", schema.toJson())
				.set("value", value != null && !value.isEmpty() ? JsonArray.wrap(value) : null);
	}

	@Override
	public String toString() {return toJson().toString();}

	public static Field valueOf(String string) {return valueOf(JsonObject.parse(string));}

	public static Field valueOf(JsonObject jsonObject)
	{
		Field field =
				new Field(Schema.valueOf(jsonObject.getJsonObject("schema")
						.orElse(jsonObject)));
		field.setValue(Value.of(jsonObject.get("value")));
		return field;
	}

	public void validate() {schema.validate(getValue());}

	public void validate(Field field) throws AppException
	{
		if (!schema.equals(field.schema))
			throw new AppException("Field schema mismatch");
		field.validate();
	}

	public record Schema(@Name
						 @Description
						 String id,
	                     @Name
						 @Description
						 String name,
	                     @Name
						 @Description
						 Size size,
	                     @Name
						 @Required
						 @Description
						 boolean required,
	                     @Name
						 @Description
						 Integer maxlength,
	                     @Name
						 @Description
						 String mask,
	                     @Name
						 @Description
						 String description,
	                     @Name
						 @Description
						 Pattern pattern,
	                     @Name
						 @Description
						 boolean readonly,
	                     @Name
						 @Required
						 @Description
						 boolean multiple,
	                     @Name
						 @Description
						 @gate.annotation.Converter(CollectionConverter.class)
						 List<String> options)
	{
		public Schema
		{
			options = options != null
					? List.copyOf(options) : List.of();
		}

		public static Schema valueOf(JsonObject jsonObject)
		{
			return Schema.builder()
					.id(jsonObject.getString("id").orElse(null))
					.name(jsonObject.getString("name").orElse(null))
					.size(jsonObject.getString("size").map(Size::parse).orElse(null))
					.required(jsonObject.getBoolean("required").orElse(Boolean.FALSE))
					.maxlength(jsonObject.getInt("maxlength").orElse(null))
					.mask(jsonObject.getString("mask").orElse(null))
					.description(jsonObject.getString("description").orElse(null))
					.pattern(jsonObject.getObject("pattern", Pattern.class).orElse(null))
					.readonly(jsonObject.getBoolean("readonly").orElse(Boolean.FALSE))
					.multiple(jsonObject.getBoolean("multiple").orElse(Boolean.FALSE))
					.options(jsonObject.getJsonElement("options").map(Options::of).orElse(new Options()))
					.build();
		}

		public List<Constraint.Implementation<?>> getConstraints()
		{
			List<Constraint.Implementation<?>> constraints = new ArrayList<>();
			if (required())
				constraints.add(new Required.Implementation("required"));
			if (maxlength() != null)
				constraints.add(new Maxlength.Implementation(maxlength()));
			if (pattern() != null)
				constraints.add(new gate.constraint.Pattern.Implementation(pattern()));
			return constraints;
		}

		public JsonObject toJson()
		{
			return new JsonObject()
					.setString("id", id())
					.setString("name", name())
					.setString("mask", mask())
					.setInt("maxlength", maxlength())
					.setObject("size", Size.class, size())
					.setString("description", description())
					.setObject("pattern", Pattern.class, pattern())
					.setBoolean("readonly", readonly() ? true : null)
					.setBoolean("multiple", multiple() ? true : null)
					.setBoolean("required", required() ? true : null)
					.set("options", options != null && !options.isEmpty() ? JsonArray.wrap(options) : null);
		}

		public void validate(List<String> value) throws AppException
		{
			if (required() && value.isEmpty())
				throw new AppException(String.format("Field %s is required", name()));

			if (!multiple() && value.size() > 1)
				throw new AppException(String.format("Field %s does not accept multiple values", name()));

			if (pattern() != null && value.stream().anyMatch(e -> !pattern().matcher(e).matches()))
				throw new AppException(String.format("Invalid format for field %s", name()));

			if (!options().isEmpty())
			{
				if (maxlength() != null && value.size() > maxlength())
					throw new AppException(String.format("Field %s accepts at most %s options", name(), maxlength()));

				if (!new HashSet<>(options()).containsAll(value))
					throw new AppException(String.format("%s is not a valid option for field %s", value, name()));
			} else if (maxlength() != null
			           && value.stream().mapToInt(String::length).sum() > maxlength())
				throw new AppException(String.format("Field %s accepts at most %s characters", name(), maxlength()));
		}

		public enum Size
		{
			@Name
			ONE,
			@Name
			TWO,
			@Name
			FOUR,
			@Name
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

		public static Builder builder()
		{
			return new Builder();
		}

		public static Builder builder(Schema schema)
		{
			return new Builder(schema);
		}

		public static final class Builder
		{
			private String id;
			private String name;
			private Size size;
			private boolean required;
			private Integer maxlength;
			private String mask;
			private String description;
			private Pattern pattern;
			private boolean readonly;
			private boolean multiple;
			private List<String> options = List.of();

			private Builder()
			{
			}

			private Builder(Schema schema)
			{
				this.id = schema.id();
				this.name = schema.name();
				this.size = schema.size();
				this.required = schema.required();
				this.maxlength = schema.maxlength();
				this.mask = schema.mask();
				this.description = schema.description();
				this.pattern = schema.pattern();
				this.readonly = schema.readonly();
				this.multiple = schema.multiple();
				this.options = schema.options();
			}

			public Builder id(String id)
			{
				this.id = id;
				return this;
			}

			public Builder name(String name)
			{
				this.name = name;
				return this;
			}

			public Builder size(Size size)
			{
				this.size = size;
				return this;
			}

			public Builder required(boolean required)
			{
				this.required = required;
				return this;
			}

			public Builder maxlength(Integer maxlength)
			{
				this.maxlength = maxlength;
				return this;
			}

			public Builder mask(String mask)
			{
				this.mask = mask;
				return this;
			}

			public Builder description(String description)
			{
				this.description = description;
				return this;
			}

			public Builder pattern(Pattern pattern)
			{
				this.pattern = pattern;
				return this;
			}

			public Builder readonly(boolean readonly)
			{
				this.readonly = readonly;
				return this;
			}

			public Builder multiple(boolean multiple)
			{
				this.multiple = multiple;
				return this;
			}

			public Builder options(List<String> options)
			{
				this.options = options != null
						? List.copyOf(options) : List.of();
				return this;
			}

			@SuppressWarnings("unchecked") public Builder options(String options)
			{
				this.options = (List<String>) Converter.fromString(List.class, options);
				return this;
			}

			public Schema build()
			{
				return new Schema(id,
						name,
						size,
						required,
						maxlength,
						mask,
						description,
						pattern,
						readonly,
						multiple,
						options);
			}
		}

		public static class Options extends ArrayList<String>
		{
			private Options() {}

			private Options(Collection<String> values) {super(values != null ? values : List.of());}

			public static Options of(JsonElement jsonElement)
			{
				if (jsonElement == null || jsonElement instanceof JsonNull)
					return new Options();
				if (jsonElement instanceof JsonArray jsonArray)
					return jsonArray.stream().filter(e -> e instanceof JsonString)
							.map(JsonString.class::cast)
							.map(JsonString::unwrap)
							.collect(Collectors.toCollection(Options::new));
				if (jsonElement instanceof JsonString jsonString)
					return valueOf(jsonString.unwrap());
				throw new IllegalStateException("Invalid value");
			}

			@SuppressWarnings("unchecked")
			public static Options valueOf(String value) {return new Options((List<String>) Converter.fromString(List.class, value));}

			public static Options of(Collection<String> values) {return new Options(values);}

			public static Options of(String... values) {return new Options(List.of(values));}

			public static Options of(String value) {return new Options(value != null ? List.of(value) : List.of());}

			public int length() {return stream().mapToInt(String::length).sum();}

			public JsonArray toJson() {return JsonArray.wrap(this);}
		}
	}

	public static final class Builder
	{
		private Value value = new Value();
		private final Schema.Builder delegate = Schema.builder();

		private Builder() {}

		public Builder id(String id)
		{
			delegate.id(id);
			return this;
		}

		public Builder name(String name)
		{
			delegate.name(name);
			return this;
		}

		public Builder size(Field.Schema.Size size)
		{
			delegate.size(size);
			return this;
		}

		public Builder required(boolean required)
		{
			delegate.required(required);
			return this;
		}

		public Builder maxlength(Integer maxlength)
		{
			delegate.maxlength(maxlength);
			return this;
		}

		public Builder mask(String mask)
		{
			delegate.mask(mask);
			return this;
		}

		public Builder description(String description)
		{
			delegate.description(description);
			return this;
		}

		public Builder pattern(Pattern pattern)
		{
			delegate.pattern(pattern);
			return this;
		}

		public Builder readonly(boolean readonly)
		{
			delegate.readonly(readonly);
			return this;
		}

		public Builder multiple(boolean multiple)
		{
			delegate.multiple(multiple);
			return this;
		}

		public Builder options(List<String> options)
		{
			delegate.options(options);
			return this;
		}

		public Builder options(String options)
		{
			delegate.options(options);
			return this;
		}

		public Builder value(Value value)
		{
			this.value = value != null ? value : new Value();
			return this;
		}

		public Field build() {return new Field(delegate.build()).setValue(value);}
	}

	public static Builder builder() {return new Builder();}

	public static class Value extends ArrayList<String>
	{
		private Value() {}

		private Value(Collection<String> values) {super(values != null ? values : List.of());}

		public static Value of(JsonElement jsonElement)
		{
			if (jsonElement == null || jsonElement instanceof JsonNull)
				return new Value();
			if (jsonElement instanceof JsonArray jsonArray)
				return jsonArray.stream().filter(e -> e instanceof JsonString)
						.map(JsonString.class::cast)
						.map(JsonString::unwrap)
						.collect(Collectors.toCollection(Value::new));
			if (jsonElement instanceof JsonString jsonString)
				return valueOf(jsonString.unwrap());
			throw new IllegalStateException("Invalid value");
		}

		public static Value of() {return new Value();}

		public static Value of(String... values) {return new Value(List.of(values));}

		public static Value of(Collection<String> values) {return new Value(values);}

		public static Value of(String value) {return new Value(value != null ? List.of(value) : List.of());}

		@SuppressWarnings("unchecked")
		public static Value valueOf(String value) {return new Value((List<String>) Converter.fromString(List.class, value));}

		public int length() {return stream().mapToInt(String::length).sum();}

		public JsonArray toJson() {return JsonArray.wrap(this);}
	}
}