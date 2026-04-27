package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.type.IDS;

import java.util.Collections;
import java.util.List;

public class IDSConverter implements Converter {

	@Override
	public List<Constraint.Implementation<?>> getConstraints() {
		return Collections.emptyList();
	}

	@Override
	public String render(Class<?> type, Object object) {
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format) {
		return object != null ? String.format(format, object.toString()) : "";
	}

	@Override
	public String toString(Class<?> type, Object object) {
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) {
		return string != null && string.trim().length() > 0 ? new IDS(string) : null;
	}

}
