package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.BrasilianDocumentConverter;

@Converter(BrasilianDocumentConverter.class)
public interface BrasilianDocument
{

	public static BrasilianDocument of(String string) throws IllegalArgumentException
	{
		if (string != null)
		{
			if (CPF.RAW.matcher(string).matches())
				return CPF.of(string);
			if (CPF.FORMATTED.matcher(string).matches())
				return CPF.of(string);
			if (CNPJ.RAW.matcher(string).matches())
				return CNPJ.of(string);
			if (CNPJ.FORMATTED.matcher(string).matches())
				return CNPJ.of(string);
		}

		throw new IllegalArgumentException(string + " is not a valid brasilian document");
	}
}
