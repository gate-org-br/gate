package gate.error;

import java.util.List;

/**
 * Signals if a duplicated object was found.
 */
public class DuplicateException extends AppException
{

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE
			= "Tentativa de processar objeto duplicado";

	/**
	 * Constructs an DuplicateException with the default message.
	 */
	public DuplicateException()
	{
		super(DEFAULT_MESSAGE);
	}

	/**
	 * Constructs an AppException with the specified detail message.
	 *
	 * @param message The detail message, which are saved for later retrieval by the getMessages() or getMessage()
	 * method
	 */
	public DuplicateException(String message)
	{
		super(message);
	}

	/**
	 * Throws a DuplicateException with the specified message if the specified list has duplicates.
	 *
	 * 
	 * @param list the list to be tested for duplication
	 * @param message the message of the DuplicateException to be thrown if the list had duplicates
	 *
	 * @return the same list passed as the first parameter
	 *
	 * @throws gate.error.DuplicateException if the specified list has duplicates
	 */
	public static <T> List<T> check(List<T> list, String message) throws DuplicateException
	{
		for (int i = 0; i < list.size(); i++)
			for (int j = i + 1; j < list.size(); j++)
				if (list.get(i).equals(list.get(j)))
					throw new DuplicateException(message);
		return list;
	}

	/**
	 * Throws a DuplicateException with the default message if the specified list has duplicates.
	 *
	 * 
	 * @param list the list to be tested for duplication
	 *
	 * @return the same list passed as the first parameter
	 *
	 * @throws gate.error.DuplicateException if the specified list has duplicates
	 */
	public static <T> List<T> check(List<T> list) throws DuplicateException
	{
		return check(list, DEFAULT_MESSAGE);
	}
}
