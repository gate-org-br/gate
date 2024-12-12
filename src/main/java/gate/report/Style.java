package gate.report;

import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import gate.type.Color;
import java.util.Objects;

public final class Style
{

	private int fontSize;
	private double width;
	private Color color;
	private TextAlign textAlign;
	private FontWeight fontWeight;
	private ListStyleType listStyleType;

	public Style()
	{
		this(12, 100, Color.BLACK, TextAlign.CENTER,
				FontWeight.NORMAL, ListStyleType.NONE);
	}

	public Style(Style style)
	{
		this(style.getFontSize(), style.getWidth(),
				style.getColor(), style.getTextAlign(),
				style.getFontWeight(), style.getListStyleType());
	}

	public Style(int fontSize, double width,
			Color color, TextAlign textAlign, FontWeight fontWeight,
			ListStyleType listStypeType)
	{
		this.fontSize = fontSize;
		this.width = width;
		this.color = color;
		this.textAlign = textAlign;
		this.fontWeight = fontWeight;
		this.listStyleType = listStypeType;
	}

	public Color getColor()
	{
		return color;
	}

	public FontWeight getFontWeight()
	{
		return fontWeight;
	}

	public TextAlign getTextAlign()
	{
		return textAlign;
	}

	public int getFontSize()
	{
		return fontSize;
	}

	public double getWidth()
	{
		return width;
	}

	public ListStyleType getListStyleType()
	{
		return listStyleType;
	}

	public void listStyleType(ListStyleType listStyleType)
	{
		this.listStyleType = Objects.requireNonNull(listStyleType);
	}

	public void textAlign(TextAlign textAlign)
	{
		this.textAlign = Objects.requireNonNull(textAlign);
	}

	public void fontWeight(FontWeight fontWeight)
	{
		this.fontWeight = Objects.requireNonNull(fontWeight);
	}

	public Style left()
	{
		this.textAlign = TextAlign.LEFT;
		return this;
	}

	public Style center()
	{
		this.textAlign = TextAlign.CENTER;
		return this;

	}

	public Style color(Color color)
	{
		this.color = color;
		return this;
	}

	public Style color(String color)
	{
		this.color = Color.of(color);
		return this;
	}

	public Style right()
	{
		this.textAlign = TextAlign.RIGHT;
		return this;
	}

	public Style justify()
	{
		this.textAlign = TextAlign.JUSTIFY;
		return this;
	}

	public Style red()
	{
		this.color = Color.RED;
		return this;
	}

	public Style green()
	{
		this.color = Color.GREEN;
		return this;
	}

	public Style blue()
	{
		this.color = Color.BLUE;
		return this;
	}

	public Style black()
	{
		this.color = Color.BLACK;
		return this;
	}

	public Style normal()
	{
		fontWeight = FontWeight.NORMAL;
		return this;

	}

	public Style bold()
	{
		fontWeight = FontWeight.BOLD;
		return this;
	}

	public Style width(double width)
	{
		this.width = Math.max(0, width);
		return this;
	}

	public Style fontSize(int fontSize)
	{
		this.fontSize = Math.max(0, fontSize);
		return this;
	}

	public Style apply(String string)
	{
		string = string.trim();
		if (!string.isEmpty())
			for (String property : string.split(";"))
			{
				String[] strings = property.trim().split(":");
				if (strings.length != 2)
					throw new IllegalArgumentException(String.format("%s is not a valid style", string));
				String name = strings[0].trim().toLowerCase();
				String value = strings[1].trim().toLowerCase();
				switch (name)
				{
					case "font-weight" ->
						fontWeight = FontWeight.of(value);
					case "color" ->
						Color.of(value);
					case "text-align" ->
						textAlign = TextAlign.of(value);
					case "list-style-type" ->
						listStyleType = ListStyleType.of(value);

					case "font-size" ->
					{
						try
						{
							fontSize = Integer.parseInt(value);
						} catch (NumberFormatException e)
						{
							throw new IllegalArgumentException(String.format("%s is not a valid font-size", value));
						}
					}
					case "width" ->
					{
						try
						{
							width = Double.parseDouble(value);
						} catch (NumberFormatException e)
						{
							throw new IllegalArgumentException(String.format("%s is not a valid width", value));
						}
					}
					default ->
						throw new IllegalArgumentException(String.format("%s is not a valid style", string));

				}
			}
		return this;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Style)
		{
			Style style = (Style) obj;
			return style.getFontSize() == fontSize
					&& style.getTextAlign() == textAlign
					&& style.getColor() == color
					&& style.getWidth() == width
					&& style.getFontWeight() == fontWeight
					&& style.getListStyleType() == listStyleType;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return (int) (textAlign.ordinal() + color.hashCode() + fontWeight.ordinal() + listStyleType.ordinal() + fontSize + width);
	}

	@Override
	public String toString()
	{
		return String.format("width: %f; color: %s; font-weight: %s; font-size: %d; text-align: %s; list-style-type: %s",
				width,
				color,
				fontWeight,
				fontSize,
				textAlign,
				listStyleType);
	}

	public static Style parse(String string)
	{
		return new Style().apply(string);
	}

	public static Style of(JsonObject object)
	{
		var style = new Style();
		object.getString("text-align").map(TextAlign::of).ifPresent(style::textAlign);
		object.getString("font-weight").map(FontWeight::of).ifPresent(style::fontWeight);
		object.getString("list-style-type").map(ListStyleType::of).ifPresent(style::listStyleType);
		object.getString("color").map(Color::of).ifPresent(style::color);
		object.getDouble("width").ifPresent(style::width);
		object.getInt("font-size").ifPresent(style::fontSize);
		return style;
	}

	public static Style of(JsonString string)
	{
		var style = new Style();
		style.apply(string.toString());
		return style;
	}

	public enum TextAlign
	{
		LEFT, RIGHT, CENTER, JUSTIFY;

		@Override
		public String toString()
		{
			return switch (this)
			{
				case LEFT ->
					"left";
				case RIGHT ->
					"right";
				case CENTER ->
					"center";
				case JUSTIFY ->
					"justify";
			};
		}

		public static TextAlign of(String string)
		{
			return switch (string)
			{
				case "center" ->
					TextAlign.CENTER;
				case "left" ->
					TextAlign.LEFT;
				case "right" ->
					TextAlign.RIGHT;
				case "justify" ->
					TextAlign.JUSTIFY;
				default ->
					throw new IllegalArgumentException(String.format("%s is not a supported text-align", string));
			};
		}
	}

	public enum FontWeight
	{
		NORMAL, BOLD;

		@Override
		public String toString()
		{
			return switch (this)
			{
				case NORMAL ->
					"normal";
				case BOLD ->
					"bold";
			};
		}

		public static FontWeight of(String string)
		{
			return switch (string)
			{
				case "normal" ->
					FontWeight.NORMAL;
				case "bold" ->
					FontWeight.BOLD;
				default ->
					throw new IllegalArgumentException(String.format("%s is not a supported font-weight", string));
			};
		}
	}

	public enum ListStyleType
	{
		NONE, DISC, DECIMAL, LOWER_ALPHA;

		@Override
		public String toString()
		{
			return switch (this)
			{
				case NONE ->
					"none";
				case DISC ->
					"disc";
				case DECIMAL ->
					"decimal";
				case LOWER_ALPHA ->
					"lower-alpha";
			};
		}

		public static ListStyleType of(String string)
		{
			return switch (string)
			{
				case "none" ->
					ListStyleType.NONE;
				case "disc" ->
					ListStyleType.DISC;
				case "decimal" ->
					ListStyleType.DECIMAL;
				case "lower-alpha" ->
					ListStyleType.LOWER_ALPHA;
				default ->
					throw new IllegalArgumentException(String.format("%s is not a supported list-style-type", string));
			};
		}
	}
}
