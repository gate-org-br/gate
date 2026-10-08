package gate.lang.json;

import java.io.Serializable;

/**
 * Represents any scalar JSON element.
 *
 * @author davins
 */
public interface JsonScalar extends Serializable
{

	/**
	 * Gets the scalar value associated with this JSON scalar element.
	 *
	 * @return the value associated with this JSON scalar element.
	 */
	Object getScalarValue();
}