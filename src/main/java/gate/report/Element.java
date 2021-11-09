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
	 *
	 * @return the same object, for chained invocations
	 */
	public Element style(Style style)
	{
		Objects.requireNonNull(style);
		this.style = style;
		return this;
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

	/**
	 * Remove empty values from element.
	 *
	 * @return the same object, for chained invocations
	 */
	public Element compact()
	{
		return this;
	}

	/**
	 * Returns true if the element is empty.
	 *
	 * @return true if the element is empty
	 */
	public boolean isEmpty()
	{
		return false;
	}
}
