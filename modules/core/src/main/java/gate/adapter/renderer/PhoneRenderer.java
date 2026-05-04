package gate.adapter.renderer;

import gate.type.br.Phone;

import java.util.regex.Pattern;

public class PhoneRenderer implements Renderer
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

	@Override
	public String render(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		String value = ((Phone) object).getValue();

		if (XZEROZERO.matcher(value).matches())
			return String.format("(%c%c%c) %c%c%c%c%c%c%c",
					value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4),
					value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9));

		if (ZEROXZEROZERO.matcher(value).matches())
			return String.format("(%c%c%c%c) %c%c%c%c%c%c%c",
					value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4),
					value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10));

		if (FIX.matcher(value).matches())
			return String.format("%c%c%c%c-%c%c%c%c",
					value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3),
					value.charAt(4), value.charAt(5), value.charAt(6), value.charAt(7));

		if (MOB.matcher(value).matches())
			return String.format("%c%c%c%c%c-%c%c%c%c",
					value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4),
					value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8));

		if (DDD_FIX.matcher(value).matches())
			return String.format("(%c%c) %c%c%c%c-%c%c%c%c",
					value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4),
					value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9));

		if (DDD_MOB.matcher(value).matches())
			return String.format("(%c%c) %c%c%c%c%c-%c%c%c%c",
					value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4),
					value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10));

		if (OP_DDD_FIX.matcher(value).matches())
			return String.format("[%c%c] (%c%c) %c%c%c%c-%c%c%c%c",
					value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4),
					value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11));

		if (OP_DDD_MOB.matcher(value).matches())
			return String.format("[%c%c] (%c%c) %c%c%c%c%c-%c%c%c%c",
					value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4),
					value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11), value.charAt(12));

		if (ZERO_OP_DDD_FIX.matcher(value).matches())
			return String.format("[%c%c%c] (%c%c) %c%c%c%c-%c%c%c%c",
					value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4),
					value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11), value.charAt(12));

		if (ZERO_OP_DDD_MOB.matcher(value).matches())
			return String.format("[%c%c%c] (%c%c) %c%c%c%c%c-%c%c%c%c",
					value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4),
					value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11), value.charAt(12), value.charAt(13));

		return value;
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}
}
