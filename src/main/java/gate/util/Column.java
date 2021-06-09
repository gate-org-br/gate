package gate.util;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Used to extract data from strings
 *
 * 
 */
public interface Column<T>
{

	public abstract T get(String string);

	/**
	 * Create a column to extract data from a string and return it as a java object
	 *
	 * @param min column start position
	 * @param max column end position
	 *
	 * @return A java string representing the content of the extracted data or null if the data is blank
	 */
	public static Column<String> of(int min, int max)
	{
		return e ->
		{
			String value = e.substring(min, max).trim();
			return !value.isEmpty() ? value : null;
		};

	}

	/**
	 * Create a column to extract data from a string and return it as a java object
	 *
	 * 
	 * @param min column start position
	 * @param max column end position
	 * @param converter function to be used to converted the extracted string to a java object if the extracted string is not blank
	 *
	 * @return A java object representing the content of the extracted data or null if the data is blank
	 */
	public static <T> Column<T> of(int min, int max, Function<String, T> converter)
	{
		return e ->
		{
			String value = e.substring(min, max).trim();
			return !value.isEmpty() ? converter.apply(value) : null;
		};
	}

	/**
	 * Create a column to extract data from a string and return it as a string
	 *
	 * @param min column start position
	 * @param max column end position
	 * @param cleanup used to remove characters from the extracted string
	 *
	 * @return A java string representing the content of the extracted data or null if the data is blank
	 */
	public static Column<String> of(int min, int max, Pattern cleanup)
	{
		return e ->
		{
			String value = cleanup.matcher(e.substring(min, max)).replaceAll("");
			return !value.isEmpty() ? value : null;
		};
	}

	/**
	 * Create a column to extract data from a string and return it as a java object
	 *
	 * 
	 * @param min column start position
	 * @param max column end position
	 * @param cleanup used to remove characters from the extracted string before conversion to a java object
	 * @param converter function to be used to converted the extracted string to a java object if the extracted string is not blank
	 *
	 * @return A java object representing the content of the extracted data or null if the data is blank
	 */
	public static <T> Column<T> of(int min, int max, Pattern cleanup, Function<String, T> converter)
	{
		return e ->
		{
			String value = cleanup.matcher(e.substring(min, max)).replaceAll("");
			return !value.isEmpty() ? converter.apply(value) : null;
		};
	}
}
