package gate.type.br;

import gate.annotation.Converter;
import gate.converter.custom.BrasilianDocumentConverter;

@Converter(BrasilianDocumentConverter.class)
public interface BrasilianDocument
{

	static BrasilianDocument of(String string) throws IllegalArgumentException
	{
		if (string == null)
			throw new IllegalArgumentException("null is not a valid Brazilian document");

		return switch (string.length())
		{
			case 11 -> CPF.of(string);
			case 18 -> CNPJ.of(string);
			case 14 ->
			{
				long value = CPF.toLong(string);
				if (value >= 0)
					yield CPF.of(value);
				yield CNPJ.of(string);
			}
			default -> throw new IllegalArgumentException(string + " is not a valid Brazilian document");
		};
	}
}