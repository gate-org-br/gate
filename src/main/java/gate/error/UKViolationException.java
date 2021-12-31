package gate.error;

import java.sql.SQLException;

/**
 * Signals an attempt to violate a database unique key constraint.
 */
public class UKViolationException extends ConstraintViolationException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an UKViolationException with the specified cause.
	 *
	 * @param cause The cause, which is saved for later retrieval by the getCause() method. A null value is permitted, and indicates that the cause is
	 * nonexistent or unknown.
	 */
	UKViolationException(SQLException cause)
	{
		super(cause, "Tentativa de inserir registro duplicado");
	}

	UKViolationException(SQLException cause, String message)
	{
		super(cause, message);
	}
}
