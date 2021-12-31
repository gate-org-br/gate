package gate.error;

import gate.sql.Link;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public DatabaseException(Throwable cause)
	{
		super(cause.getMessage(), cause);
	}

	public DatabaseException(String message)
	{
		super(message);
	}

	public static void handle(Link link, SQLException cause)
		throws DatabaseException,
		ConstraintViolationException
	{

		try
		{
			DatabaseMetaData metadata = link.getConnection().getMetaData();
			if (metadata != null)
				if ("MYSQL".equalsIgnoreCase(metadata.getDatabaseProductName()))
				{
					switch (cause.getErrorCode())
					{
						case 1062:
						case 1586:
							Matcher matcher
								= Pattern.compile("Duplicate entry '([^']+)' for key")
									.matcher(cause.getMessage());

							if (!matcher.matches())
								throw new UKViolationException(cause);

							throw new UKViolationException(cause, "Tentativa de inserir registro duplicado: "
								+ matcher.group(1));

						case 1216:
						case 1217:
						case 1451:
						case 1452:
							throw new FKViolationException(cause);
					}
				}

			String SQLstate = cause.getSQLState();
			if (SQLstate != null)
				switch (SQLstate)
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
		} catch (SQLException ex)
		{
			throw new DatabaseException(ex);
		}
	}
}
