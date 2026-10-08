package gate.lang.csv;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Row extends ArrayList<String>
{

	private final long index;
	private final String text;

	public Row(long index, String text)
	{
		this.index = index;
		this.text = text;
	}

	/**
	 * Return the index of the line on the SV file
	 *
	 * @return the index of the line on the SV file
	 */
	public long getIndex()
	{
		return index;
	}

	/**
	 * Return the text of the line on the SV file
	 *
	 * @return the text of the line on the SV file
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Creates a row with the specified index, text and values
	 *
	 * @param index the index of the new row
	 * @param text the text of the new row
	 * @param values the values of the new row
	 * @return a new row with the specified index, text and values
	 */
	public static Row of(long index, String text, String... values)
	{
		return Stream.of(values).collect(Collectors.toCollection(() -> new Row(index, text)));
	}
}
