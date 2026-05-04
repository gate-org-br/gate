package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.type.br.Phone;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Description("Campos de número de telefone devem ser compostos apenas por dígitos")
public class PhoneConverter implements Converter
{

	private static final Pattern XZEROZERO = Pattern.compile("^[1-9][0]{2}[0-9]{7}$");

	private static final Pattern ZEROXZEROZERO = Pattern.compile("^0[1-9][0]{2}[0-9]{7}$");

	private static final Pattern FIX = Pattern.compile("^[2-9][0-9]{7}$");
	private static final Pattern MOB = Pattern.compile("^9[0-9]{8}$");

	private static final Pattern DDD_FIX = Pattern.compile("^[1-9]{2}[2-9][0-9]{7}$");
	private static final Pattern DDD_MOB = Pattern.compile("^[1-9]{2}9[0-9]{8}$");

	private static final Pattern OP_DDD_FIX = Pattern.compile("^[1-9]{2}[1-9]{2}[2-9][0-9]{7}$");
	private static final Pattern OP_DDD_MOB = Pattern.compile("^[1-9]{2}[1-9]{2}9[0-9]{8}$");

	private static final Pattern ZERO_OP_DDD_FIX = Pattern.compile("^0[1-9]{2}[1-9]{2}[2-9][0-9]{7}$");
	private static final Pattern ZERO_OP_DDD_MOB = Pattern.compile("^0[1-9]{2}[1-9]{2}9[0-9]{8}$");

	public List<Constraint.Implementation<?>> getConstraints()
	{
		return new ArrayList<>();
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		try
		{
			if (string == null || string.isEmpty())
				return null;
			return new Phone(string);
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(string.concat(" não é um número de telefone válido."));
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

}