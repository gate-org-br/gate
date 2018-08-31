package gate.language;

/**
 * Provides language utility methods.
 */
public interface Language
{

	public static final Language PORTUGUESE = new Portuguese();

	/**
	 * Capitalizes a word.
	 *
	 * @param string the word to be capitalized
	 *
	 * @return the capitalized word
	 */
	public String capitalizeWord(String string);

	/**
	 * Capitalizes each word a name ignoring connectives.
	 *
	 * @param string the name to be capitalized
	 *
	 * @return the capitalized name
	 */
	public String capitalizeName(String string);
}
