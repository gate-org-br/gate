package gate.type.mime;

import gate.lang.contentType.ContentType;
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
	ContentType getContentType();

}
