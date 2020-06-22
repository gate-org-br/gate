package gate.sql;

public class Formatter
{

	public static String format(String sql, Object... args)
	{
		int index = 0;
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < sql.length(); i++)
			if (sql.charAt(i) == '@')
				result.append(args[index++].toString());
			else
				result.append(sql.charAt(i));
		return result.toString();
	}

}
