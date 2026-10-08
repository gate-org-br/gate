package gate.type.br;

public interface BrasilianDocument
{

	static BrasilianDocument valueOf(String string) throws IllegalArgumentException
	{
		if (string == null)
			throw new IllegalArgumentException("null is not a valid Brazilian document");

		return switch (string.length())
		{
			case 11 -> CPF.valueOf(string);
			case 18 -> CNPJ.valueOf(string);
			case 14 ->
			{
				long value = CPF.toLong(string);
				if (value >= 0)
					yield CPF.valueOf(value);
				yield CNPJ.valueOf(string);
			}
			default -> throw new IllegalArgumentException(string + " is not a valid Brazilian document");
		};
	}
}