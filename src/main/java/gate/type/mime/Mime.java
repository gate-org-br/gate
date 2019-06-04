package gate.type.mime;

import java.io.Serializable;

/**
 * Represents any mime element.
 */
public interface Mime extends Serializable
{

	/**
	 * Gets the mime content type.
	 *
	 * @return the mime content type
	 */
	String getType();

	/**
	 * Gets the mime content subtype.
	 *
	 * @return the mime content subtype
	 */
	String getSubType();
}
