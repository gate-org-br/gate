package gate.type;

import java.text.ParseException;

public interface Formatter<T>
{

	T parse(String string) throws ParseException;

	String format(T object);
}
