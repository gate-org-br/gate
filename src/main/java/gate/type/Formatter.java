package gate.type;

import java.text.ParseException;

public interface Formatter<T>
{

	public T parse(String string) throws ParseException;

	public String format(T object);
}
