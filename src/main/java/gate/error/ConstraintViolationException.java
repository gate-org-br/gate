package gate.error;

import java.sql.SQLException;

/**
 * Signals an attempt to violate a database constraint.
 *
 * @author davins
 */
public class ConstraintViolationException extends AppException
{

	private static final long serialVersionUID = 1L;

	ConstraintViolationException(SQLException cause, String message)
	{
		super(cause, message);

	}

	@Override
	public synchronized SQLException getCause()
	{
		return (SQLException) super.getCause();
	}

	ConstraintViolationException(SQLException cause)
	{
		this(cause, "Tentativa de violar restrição de integridade");
	}
}
