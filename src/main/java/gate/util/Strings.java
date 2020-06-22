package gate.util;

public class Strings
{

	public static boolean empty(String string)
	{
		if (string != null & !string.isEmpty())
			for (int i = 0; i < string.length(); i++)
				if (!Character.isWhitespace(string.charAt(i)))
					return false;
		return true;
	}
}
