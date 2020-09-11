package gate.lang.csv;

import java.util.ArrayList;

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
}
