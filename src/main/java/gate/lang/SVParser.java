package gate.lang;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface SVParser extends AutoCloseable, Iterable<List<String>>
{

	/**
	 * Extracts the next line of the previously specified CSV formatted
	 * {@link java.io.Reader} as a String List.
	 *
	 * @return an Optional describing the row returned as a String List of
	 * an empty Optional if there are no more rows to be read
	 * @throws java.io.IOException If an I/O error occurs
	 */
	public Optional<List<String>> parseLine() throws IOException;

	/**
	 * Try to skip a number of lines from the previously specified CSV
	 * formatted {@link java.io.Reader}.
	 *
	 * @param lines the number of lines to be skipped
	 *
	 * @return the number of lines not skipped
	 *
	 * @throws java.io.IOException If an I/O error occurs
	 */
	public long skip(long lines) throws IOException;

	/**
	 * Return the current line number.
	 *
	 * @return the current line number
	 */
	public long getLineNumber();

	/**
	 * Returns a sequential Stream with this parser as its source.
	 *
	 * @return a sequential Stream over the elements in this parser
	 */
	public Stream<List<String>> stream();
}
