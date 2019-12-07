package gate.language;

import java.text.Normalizer;
import java.util.Comparator;

/**
 * Provides language utility methods.
 */
public interface Language extends Comparator<String>
{

	Language PORTUGUESE = new Portuguese();

	/**
	 * Capitalizes each word a string ignoring connectives.
	 *
	 * @param stringToBeCapitalized the string to be capitalized
	 * @return the capitalized string if the stringToBeCapitalized is not null or null otherwise
	 */
	String capitalize(String stringToBeCapitalized);

	/**
	 * Removes replaces all non ASCII characters of the specified string with ASCII characters
	 *
	 * @param nonASCIIString the string from witch non ASCII characters are to be replaced
	 * @return the ASCII representation of nonASCIIString if it is not null or null otherwise
	 */
	default String toASCII(String nonASCIIString)
	{
		return Normalizer.normalize(nonASCIIString,
			Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	/**
	 * Compares the ASCII representation of 2 strings ignoring case.
	 * <p>
	 * Both parameters will have all non ASCII characters replaced with ASCII characters before comparison.
	 *
	 * @param string1 the first string to be compared
	 * @param string2 the second string to be compared
	 * @return the value 0 if string1 is equal to this string2 or if both are null; a value less than 0 if this string1
	 * is lexicographically less than the string2 or string2 is null; and a value greater than 0 if string1 is
	 * lexicographically greater than the string2 or string2 is null.
	 */
	@Override
	default int compare(String string1, String string2)
	{
		if (string1 == null && string2 == null)
			return 0;
		if (string1 != null && string2 == null)
			return 1;
		if (string1 == null && string2 != null)
			return -1;
		return toASCII(string1).toLowerCase().compareTo(toASCII(string2).toLowerCase());
	}

	/**
	 * Returns true if and only if the ASCII representation of a string contains the ASCII representation of another one
	 * ignoring case.
	 * <p>
	 * Both parameters will have all non ASCII characters replaced with ASCII characters before execution.
	 *
	 * @param string    the string to be parsed
	 * @param substring the substring to search for
	 * @return true if the specified string contains the specified substring or both are null, false otherwise
	 */
	default boolean contains(String string, String substring)
	{
		if (string == null)
			return substring == null;
		else if (substring == null)
			return false;
		else
			return toASCII(string).toLowerCase()
				.contains(toASCII(substring).toLowerCase());
	}
}
