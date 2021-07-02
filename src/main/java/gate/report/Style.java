package gate.report;

import gate.type.Color;

public final class Style
{

	private int fontSize;
	private double width;
	private Color color;
	private TextAlign textAlign;
	private FontWeight fontWeight;

	public Style()
	{
		this(12, 100, Color.BLACK, TextAlign.CENTER, FontWeight.NORMAL);
	}

	public Style(Style style)
	{
		this(style.getFontSize(), style.getWidth(),
			style.getColor(), style.getTextAlign(), style.getFontWeight());
	}

	public Style(int fontSize, double width,
		Color color, TextAlign textAlign, FontWeight fontWeight)
	{
		this.fontSize = fontSize;
		this.width = width;
		this.color = color;
		this.textAlign = textAlign;
		this.fontWeight = fontWeight;
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
					case "font-weight":
						switch (value)
						{
							case "bold":
								fontWeight = FontWeight.BOLD;
								break;
							case "normal":
								fontWeight = FontWeight.NORMAL;
								break;
							default:
								throw new IllegalArgumentException(String.format("%s is not a supported font-weight", value));
						}
						break;
					case "color":
						switch (value)
						{
							case "black":
								color = Color.BLACK;
								break;
							case "red":
								color = Color.RED;
								break;
							case "blue":
								color = Color.BLUE;
								break;
							case "green":
								color = Color.GREEN;
								break;
							default:
								color = Color.of(value);
						}
						break;

					case "text-align":
						switch (value)
						{
							case "left":
								textAlign = TextAlign.LEFT;
								break;
							case "right":
								textAlign = TextAlign.RIGHT;
								break;
							case "center":
								textAlign = TextAlign.CENTER;
								break;
							case "justify":
								textAlign = TextAlign.JUSTIFY;
								break;
							default:
								throw new IllegalArgumentException(String.format("%s is not a supported text-align", value));
						}
						break;
					case "font-size":
						try
						{
							fontSize = Integer.parseInt(value);
					} catch (NumberFormatException e)
					{
						throw new IllegalArgumentException(String.format("%s is not a valid font-size", value));
					}
					break;
					case "width":
						try
						{
							width = Double.parseDouble(value);
					} catch (NumberFormatException e)
					{
						throw new IllegalArgumentException(String.format("%s is not a valid width", value));
					}

					break;
					default:
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
				&& style.getFontWeight() == fontWeight;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return (int) (textAlign.ordinal() + color.hashCode() + fontWeight.ordinal() + fontSize + width);
	}

	@Override
	public String toString()
	{
		return String.format("width: %f; color: %s; font-weight: %s; font-size: %d; text-align: %s",
			width,
			color,
			fontWeight,
			fontSize,
			textAlign);
	}

	public static Style parse(String string)
	{
		return new Style().apply(string);
	}

	public enum TextAlign
	{
		LEFT("left"), RIGHT("right"), CENTER("center"), JUSTIFY("justify");

		TextAlign(String string)
		{
			this.string = string;
		}

		private final String string;

		@Override
		public String toString()
		{
			return string;
		}

	}

	public enum FontWeight
	{
		NORMAL("normal"), BOLD("bold");

		FontWeight(String string)
		{
			this.string = string;
		}

		private final String string;

		@Override
		public String toString()
		{
			return string;
		}

	}

}
