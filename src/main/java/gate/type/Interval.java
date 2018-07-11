package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.IntervalConverter;
import java.io.Serializable;
import java.util.regex.Pattern;

@Converter(IntervalConverter.class)
public class Interval implements Serializable
{

	public static Pattern SPLIT = Pattern.compile("-");
	public static Pattern CHECK = Pattern.compile("^ *[0-9]+ *(- *[0-9]+ *)?$");

	private int v1;
	private int v2;

	public Interval(int v1, int v2)
	{
		if (v1 < 0)
			throw new IllegalArgumentException("V1 precisa > 0.");
		if (v2 < 0)
			throw new IllegalArgumentException("V2 precisa > 0.");
		if (v1 > v2)
			throw new IllegalArgumentException("V1 precisa ser < que V2.");
		this.v1 = v1;
		this.v2 = v2;
	}

	public Interval(String value)
	{
		if (value == null || !CHECK.matcher(value).matches())
			throw new IllegalArgumentException("Um intervalo deve ser uma faixa de dois números separados pelo caractere traço (-).");
		String[] values = SPLIT.split(value);
		v1 = Integer.parseInt(values[0].trim());
		v2 = Integer.parseInt(values[values.length == 2 ? 1 : 0].trim());
		if (v1 > v2)
			throw new IllegalArgumentException("V1 precisa ser <= que V2.");
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Interval && ((Interval) obj).v1 == v1 && ((Interval) obj).v2 == v2;
	}

	@Override
	public int hashCode()
	{
		return v1 + v2;
	}

	@Override
	public String toString()
	{
		return v1 != v2 ? String.format("%d - %d", v1, v2) : String.valueOf(v1);
	}
}
