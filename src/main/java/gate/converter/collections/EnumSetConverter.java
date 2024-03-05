package gate.converter.collections;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonString;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class EnumSetConverter implements Converter {

	@Override
	public List<Constraint.Implementation<?>> getConstraints() {
		return Collections.emptyList();
	}

	@Override
	public String getDescription() {
		return "Lista de opções";
	}

	@Override
	public String getMask() {
		return null;
	}

	@Override
	public String toText(Class<?> type, Object object) {
		return object != null ? ((EnumSet<?>) object).stream()
				.map(e -> Converter.toText(e)).collect(Collectors.joining(", ")) : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format) {
		return object != null ? String.format(format, toText(type, object)) : "";
	}

	@Override
	public String toString(Class<?> type, Object object) {
		if (object == null)
			return "";
		return ((EnumSet<?>) object).stream().map(e -> e.name())
				.map(e -> '"' + e + '"')
				.collect(() -> new StringJoiner(", ", "[", "]"), StringJoiner::add, StringJoiner::merge)
				.toString();
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException {
		if (string == null || string.isBlank())
			return null;

		Class<? extends Enum> enumClass = type.asSubclass(Enum.class);
		return JsonArray.parse(string).stream()
				.map(e -> (JsonString) e)
				.map(e -> e.getValue())
				.map(e -> Enum.valueOf(enumClass, e))
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(enumClass)));
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException {
		String value = rs.getString(fields);
		if (rs.wasNull())
			return null;
		return ofString(type, value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type)
			throws SQLException, ConversionException {
		String value = rs.getString(fields);
		if (rs.wasNull())
			return null;
		return ofString(type, value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException {
		if (value != null)
			ps.setString(fields++, toString(value.getClass(), value));
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}
