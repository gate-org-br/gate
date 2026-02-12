package gate.error;

import jakarta.servlet.http.HttpServletResponse;

import java.io.Serial;

/**
 * Signals that a requested resource could not be found.
 */
public class InternalServerException extends HttpException
{

    private static final String DEFAULT_MESSAGE = "Erro de sistema";

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an NotFoundException.
     */
    public InternalServerException()
    {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs an InternalServerException with the specified message.
     *
     * @param message the exception message
     */
    public InternalServerException(String message)
    {
        super(message);
    }

    /**
     * Creates an InternalServerException with a default message and the
     * underlying cause.
     *
     * @param cause the original exception that caused the failure
     */
    public InternalServerException(Throwable cause)
    {
        super(DEFAULT_MESSAGE, cause);
    }

    /**
     * Creates an InternalServerException with a specified message and the
     * underlying cause.
     *
     * @param message the exception message
     */
    public InternalServerException(String message, Throwable cause)
    {
        super(message, cause);
    }

    @Override
    public int getStatusCode()
    {
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

}
