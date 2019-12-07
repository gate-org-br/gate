package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.Form;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FormConverter implements Converter
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
		return "Campos do tipo Form devem estar no formado JSON.";
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		if (object instanceof Form)
		{
			Form form = (Form) object;
			if (form.getFields().isEmpty())
				return "";
			return form.getFields().stream().map(e -> Converter.toText(e))
				.collect(Collectors.joining("", "<fieldset>", "</fieldset>"));
		}
		return "";
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
		try
		{
			return string != null && string.trim().length() > 0
				? Form.parse(string) : null;
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(string.concat(" não é um Formulário válido."));
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		String string = rs.getString(fields);
		return rs.wasNull() ? null : Form.parse(string);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		String string = rs.getString(fields);
		return rs.wasNull() ? null : Form.parse(string);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(fields++, ((Form) value).toString());
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}

}
