package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.converter.ObjectConverter;
import gate.error.ConversionException;
import gate.type.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class FieldConverter extends ObjectConverter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Campos do tipo Field devem estar no formado JSON.";
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		if (object == null)
			return "";

		Field field = (Field) object;

		String value = Converter.toText(field.getValue());

		if (field.getSize() != null)
		{
			int size = (int) Math.pow(2, field.getSize().ordinal());
			if (Boolean.TRUE.equals(field.getMultiple()))
				return String.format("<label data-size='%d'>%s: <span style='flex-basis: 60px; overflow: auto'><label>%s</label></span></label>", size, field.getName(), value);
			else
				return String.format("<label data-size='%d'>%s: <span><label>%s</label></span></label>", size, field.getName(), value);

		} else
		{
			if (Boolean.TRUE.equals(field.getMultiple()))
				return String.format("<label>%s: <span style='flex-basis: 60px; overflow: auto'><label>%s</label></span></label>", field.getName(), value);
			else
				return String.format("<label>%s: <span><label>%s</label></span></label>", field.getName(), value);
		}
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return toText(type, object);
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;
		return Field.parse(string);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		String string = rs.getString(fields);
		return rs.wasNull() ? null : Field.parse(string);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		String string = rs.getString(fields);
		return rs.wasNull() ? null : Field.parse(string);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(fields++, value.toString());
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}
