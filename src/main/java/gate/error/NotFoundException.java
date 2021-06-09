package gate.error;

/**
 * Signals that a requested resource could not be found.
 */
public class NotFoundException extends AppException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an NotFoundException.
	 */
	public NotFoundException()
	{
		super("Tentativa de acessar registro inexistente");
	}

	/**
	 * Constructs an NotFoundException with the specified message.
	 *
	 * @param message the exception message
	 */
	public NotFoundException(String message)
	{
		super(message);
	}

	/**
	 * Throws an NotFoundException with the specified message if the specified object is null.
	 *
	 * 
	 * @param object object to be tested for null
	 *
	 * @return the same object passed as the first parameter
	 *
	 * @throws gate.error.NotFoundException if the specified object is null
	 */
	public static <T> T require(T object) throws NotFoundException
	{
		if (object == null)
			throw new NotFoundException();
		return object;
	}

	/**
	 * Throws an NotFoundException with the specified message if the specified assertion is false.
	 *
	 * @param assertion the assertion to be checked
	 *
	 * @throws gate.error.NotFoundException if the specified assertion is false
	 */
	public static void assertTrue(boolean assertion) throws NotFoundException
	{
		if (!assertion)
			throw new NotFoundException();
	}

	/**
	 * Throws an NotFoundException with the specified message if the specified assertion is true.
	 *
	 * @param assertion the assertion to be checked
	 *
	 * @throws gate.error.NotFoundException if the specified assertion is true
	 */
	public static void assertFalse(boolean assertion) throws NotFoundException
	{
		if (assertion)
			throw new NotFoundException();
	}
}
