package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.Field;
import gate.util.Toolkit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class FieldConverter implements Converter
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
		int size = Integer.parseInt(field.getSize().toString()) * 2;
		if (!Toolkit.isEmpty(field.getName()))
			if (Boolean.FALSE.equals(field.getMultiple()))
				return String.format("<label data-size='%d'>%s: <span><label>%s</label></span></label>", size, field.getName(), Converter.toText(field.getValue()));
			else
				return String.format("<label data-size='%d'>%s: <span style='flex-basis: 60px; overflow: auto'><label>%s</label></span></label>", size, field.getName(), Converter.toText(field.getValue()));
		else if (Boolean.FALSE.equals(field.getMultiple()))
			return String.format("<label data-size='%d'>&nbsp; <span style='background-color: transparent;'><label>&nbsp;</label></span></label>", size);
		else
			return String.format("<label data-size='%d'>&nbsp; <span style='flex-basis: 60px; ; overflow: auto; background-color: transparent;'><label>&nbsp;</label></span></label>", size);
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
