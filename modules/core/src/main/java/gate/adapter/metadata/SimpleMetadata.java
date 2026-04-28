package gate.adapter.metadata;

import gate.icon.Icon;

public class SimpleMetadata implements Metadata
{
	private final String name;
	private final String description;
	private final String tooltip;
	private final String placeholder;
	private final String mask;
	private final String color;
	private final String code;
	private final Icon icon;

	public SimpleMetadata()
	{
		this(builder());
	}

	protected SimpleMetadata(Builder builder)
	{
		this.name = builder.name;
		this.description = builder.description;
		this.tooltip = builder.tooltip;
		this.placeholder = builder.placeholder;
		this.mask = builder.mask;
		this.color = builder.color;
		this.code = builder.code;
		this.icon = builder.icon;
	}

	public static Builder builder() {return new Builder();}

	@Override public String name() {return name;}

	@Override public String description() {return description;}

	@Override public String tooltip() {return tooltip;}

	@Override public String placeholder() {return placeholder;}

	@Override public String mask() {return mask;}

	@Override public String color() {return color;}

	@Override public String code() {return code;}

	@Override public Icon icon() {return icon;}

	public static class Builder
	{
		private String name;
		private String description;
		private String tooltip;
		private String placeholder;
		private String mask;
		private String color;
		private String code;
		private Icon icon;

		public Builder name(String value)
		{
			this.name = value;
			return this;
		}

		public Builder description(String value)
		{
			this.description = value;
			return this;
		}

		public Builder tooltip(String value)
		{
			this.tooltip = value;
			return this;
		}

		public Builder placeholder(String value)
		{
			this.placeholder = value;
			return this;
		}

		public Builder mask(String value)
		{
			this.mask = value;
			return this;
		}

		public Builder color(String value)
		{
			this.color = value;
			return this;
		}

		public Builder code(String value)
		{
			this.code = value;
			return this;
		}

		public Builder icon(Icon value)
		{
			this.icon = value;
			return this;
		}

		public SimpleMetadata build()
		{
			return new SimpleMetadata(this);
		}
	}
}