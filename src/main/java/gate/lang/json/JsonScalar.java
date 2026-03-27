package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.handler.JsonElementHandler;
import java.io.Serializable;

/**
 * Represents any scalar JSON element.
 *
 * @author davins
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public interface JsonScalar extends Serializable
{

	/**
	 * Gets the scalar value associated with this JSON scalar element.
	 *
	 * @return the value associated with this JSON scalar element.
	 */
	public Object getScalarValue();
}
