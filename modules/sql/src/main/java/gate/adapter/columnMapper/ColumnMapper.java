package gate.adapter.columnMapper;

import gate.adapter.registry.ColumnMapperRegistry;
import gate.error.ConversionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Maps java values to and from JDBC columns.
 */
public interface ColumnMapper
{

	String SEPARATOR = "__";

	default List<String> getSufixes()
	{
		return Collections.emptyList();
	}

	default Stream<String> getColumns(String column)
	{
		return getSufixes().isEmpty() ? Stream.of(column)
				: getSufixes().stream().map(e -> column + gate.adapter.columnMapper.ColumnMapper.SEPARATOR + e);
	}

	/**
	 * Retrieves a java object of the mapper-associated type from a JDBC ResultSet.
	 *
	 * @param rs    the JDBC ResultSet from where the java object must be retrieved
	 * @param index the start index of the columns associated with the java object to be retrieved
	 * @param type  the type for the java object to be retrieved
	 * @return the java object retrieved from the specified JDBC ResultSet
	 * @throws SQLException        if a SQLException is thrown while retrieving the object from the
	 *                             JDBC ResultSet
	 * @throws ConversionException if the specified type can't be retrieved from a JDBC ResultSet
	 */
	Object readFromResultSet(ResultSet rs, int index, Class<?> type)
			throws SQLException, ConversionException;

	/**
	 * Retrieves a java object from a JDBC ResultSet.
	 *
	 * @param rs     the JDBC ResultSet from where the java object must be retrieved
	 * @param fields the name of the columns associated with the java object to be retrieved
	 * @param type   the type for the java object to be retrieved
	 * @return the java object retrieved from the specified JDBC ResultSet
	 * @throws SQLException        if a SQLException is thrown while retrieving the object from the
	 *                             JDBC ResultSet
	 * @throws ConversionException if the specified type can't be retrieved from a JDBC ResultSet
	 */
	Object readFromResultSet(ResultSet rs, String fields, Class<?> type)
			throws SQLException, ConversionException;

	/**
	 * Passes the specified object as parameters to a JDBC PreparedStatement.
	 *
	 * @param ps    the JDBC PreparedStatement to receive the specified object as parameters
	 * @param index the starting index of the parameters to be passed
	 * @param value the object to be passed as parameters to the JDBC PreparedStatement
	 * @return the index to be used on the next call to this method
	 * @throws SQLException if an SQLException is thrown while setting the parameters
	 */
	int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException;

	default int writeToPreparedStatement(ColumnWriter writer, int index, Object value) throws SQLException
	{
		return writeToPreparedStatement(writer.getPreparedStatement(), index, value);
	}

	/**
	 * Gets the column mapper associated with the specified java class.
	 *
	 * @param type java class whose associated column mapper must be returned
	 * @return the column mapper associated with the specified java class
	 */
	static ColumnMapper getColumnMapper(Class<?> type)
	{
		return ColumnMapperRegistry.INSTANCE.get(type);
	}
}