package gate.error;

import java.sql.SQLException;

import gate.annotation.Catcher;
import gate.catcher.HttpExceptionCatcher;

/**
 * Signals an attempt to violate a database constraint.
 *
 * @author davins
 */
@Catcher(HttpExceptionCatcher.class)
public class ConstraintViolationException extends AppException
{

	private static final long serialVersionUID = 1L;

	ConstraintViolationException(String message, SQLException cause)
	{
		super(message, cause);

	}

	ConstraintViolationException(SQLException cause)
	{
		this("Tentativa de violar restrição de integridade", cause);
	}

	@Override
	public synchronized SQLException getCause()
	{
		return (SQLException) super.getCause();
	}
}
