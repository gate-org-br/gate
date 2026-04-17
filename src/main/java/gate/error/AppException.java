package gate.error;

import gate.annotation.Catcher;
import gate.catcher.HttpExceptionCatcher;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serial;

/**
 * Signals that an application level exception to some sort has occurred.
 * <p>
 * This class is the general class of application level exceptions produced by user actions.
 */
@Catcher(HttpExceptionCatcher.class)
public class AppException extends HttpException
{

	@Override
	public int getStatusCode() {return HttpServletResponse.SC_BAD_REQUEST;}

	@Serial private static final long serialVersionUID = 1L;

	/**
	 * Constructs an AppException with the specified detail message.
	 *
	 * @param message The detail message, which are saved for later retrieval by the getMessages() or getMessage() method
	 */
	public AppException(String message)
	{
		super(message);
	}

	/**
	 * Constructs an AppException with the specified cause and detail message.
	 *
	 * @param cause   The cause, which is saved for later retrieval by the getCause() method. A null value is permitted, and indicates that the cause is
	 *                nonexistent or unknown.
	 * @param message The detail message, which are saved for later retrieval by the getMessages() or getMessage() method
	 */
	public AppException(String message, Throwable cause)
	{
		super(message, cause);
	}
}