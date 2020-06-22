package gate.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SearchParser implements Iterable<String>
{

	private String string;
	private final List<String> tokens
			= new ArrayList<>();

	public SearchParser(String string)
	{
		if (string != null)
		{
			int i = 0;
			char state = ' ';
			StringBuilder token = new StringBuilder();

			do
			{
				switch (string.charAt(i))
				{
					case ' ':
						switch (state)
						{
							case ' ':
								tokens.add(token.toString());
								token.setLength(0);
								state = ' ';
								break;
							case '"':
								token.append(string.charAt(i));
								break;
							case '\'':
								token.append(string.charAt(i));
								break;
						}
						break;
					case '"':
						switch (state)
						{
							case ' ':
								state = '"';
								break;
							case '"':
								tokens.add(token.toString());
								token.setLength(0);
								state = ' ';
								break;
							case '\'':
								token.append(string.charAt(i));
								break;
						}
						break;
					case '\'':
						switch (state)
						{
							case ' ':
								state = '\'';
								break;
							case '"':
								token.append(string.charAt(i));
								break;
							case '\'':
								tokens.add(token.toString());
								token.setLength(0);
								state = ' ';
								break;
						}
						break;
					default:
						token.append(string.charAt(i));
				}
			} while (++i < string.length());

			if (token.length() > 0)
				tokens.add(token.toString());
		}
	}

	public List<String> getTokens()
	{
		return Collections.unmodifiableList(tokens);
	}

	@Override
	public Iterator<String> iterator()
	{
		return getTokens().iterator();
	}

	@Override
	public String toString()
	{
		return string;
	}
}
