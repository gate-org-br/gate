package gate.lang.csv;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

public interface Parser extends AutoCloseable, Iterable<Row>
{

	/**
	 * Extracts the next line of the previously specified CSV formatted
	 * {@link java.io.Reader} as a String List.
	 *
	 * @return an Optional describing the row returned as a String List of
	 * an empty Optional if there are no more rows to be read
	 * @throws java.io.IOException If an I/O error occurs
	 */
	public Optional<Row> parseLine() throws IOException;

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
	 * Returns a sequential Stream with this parser as its source.
	 *
	 * @return a sequential Stream over the elements in this parser
	 */
	public Stream<Row> stream();
	
	/**
	 * Returns the number of processed lines.
	 *
	 * @return the number of processed lines
	 */
	public long processed();
}
