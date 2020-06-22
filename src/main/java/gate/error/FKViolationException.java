package gate.error;

import java.sql.SQLException;

/**
 * Signals an attempt to violate a database foreign key constraint.
 */
public class FKViolationException extends ConstraintViolationException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an FKViolationException with the specified cause.
	 *
	 * @param cause The cause, which is saved for later retrieval by the getCause() method. A null value is
	 * permitted, and indicates that the cause is nonexistent or unknown.
	 */
	FKViolationException(SQLException cause)
	{
		super(cause, "Tentativa de remover registro que possui outros registros associados");
	}
}
