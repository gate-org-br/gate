package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.type.mime.MimeData;
import gate.util.Strings;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class MimeDataConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string) {return !Strings.empty(string) ? MimeData.parse(string) : null;}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

}