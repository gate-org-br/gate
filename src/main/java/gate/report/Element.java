package gate.report;

import java.util.Objects;

/**
 * A generic element.
 */
public abstract class Element
{

	private Style style;

	public Element(Style style)
	{
		this.style = style;
	}

	/**
	 * Change the style of the element.
	 *
	 * @param style the new style of the element
	 */
	public void setStyle(Style style)
	{
		Objects.requireNonNull(style);
		this.style = style;
	}

	/**
	 * Gets the current column style.
	 *
	 * @return the current column style
	 */
	public Style style()
	{
		return style;
	}
}
