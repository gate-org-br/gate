package gate.error;

import gate.sql.Link;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class DatabaseException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	DatabaseException(Throwable cause)
	{
		super(cause);
	}

	public static void handle(Link link, SQLException cause)
			throws DatabaseException,
			ConstraintViolationException
	{
		try
		{
			if ("MYSQL".equals(link.getConnection().getMetaData()
				.getDatabaseProductName().toUpperCase()))
			{
				switch (cause.getErrorCode())
				{
					case 1062:
					case 1586:
						throw new UKViolationException(cause);
					case 1451:
					case 1452:
						throw new FKViolationException(cause);
				}
			}

			switch (cause.getSQLState())
			{
				case "23505":
					throw new UKViolationException(cause);
				case "23511":
					throw new FKViolationException(cause);
				case "23000":
					throw new ConstraintViolationException(cause);
				default:
					if (cause instanceof SQLIntegrityConstraintViolationException)
						throw new ConstraintViolationException(cause);
					throw new DatabaseException(cause);
			}
		} catch (SQLException e)
		{
			throw new DatabaseException(e);
		}
	}

	@Override
	public String getMessage()
	{
		return getCause().getMessage();
	}
}
