package gate.sql;

import gate.converter.Converter;
import gate.error.ConversionException;
import gate.lang.property.Property;
import gate.sql.fetcher.Fetcher;
import gate.sql.mapper.Mapper;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Enables iteration over the results of a query.
 */
public class Cursor implements AutoCloseable, Fetchable
{

	private int column = 1;
	private Command command;
	private final ResultSet rs;

	Cursor(Command command, ResultSet rs)
	{
		this.rs = rs;
	}

	@Override
	public <T> T fetch(Fetcher<T> fetcher)
	{
		return fetcher.fetch(this);
	}

	@Override
	public <T> Stream<T> stream(Mapper<T> mapper)
	{
		return StreamSupport.stream(new CursorSpliterator<T>()
		{
			@Override
			public boolean tryAdvance(Consumer<? super T> action)
			{
				if (!next())
					return false;

				action.accept(mapper.apply(Cursor.this));

				return true;

			}

		}, false);
	}

	@Override
	public char fetchChar()
	{
		return next() ? getCurrentCharValue() : 0;
	}

	@Override
	public boolean fetchBoolean()
	{
		return next() && getCurrentBooleanValue();
	}

	@Override
	public byte fetchByte()
	{
		return next() ? getCurrentByteValue() : 0;
	}

	@Override
	public short fetchShort()
	{
		return next() ? getCurrentShortValue() : 0;
	}

	@Override
	public int fetchInt()
	{
		return next() ? getCurrentIntValue() : 0;
	}

	@Override
	public long fetchLong()
	{
		return next() ? getCurrentLongValue() : 0;
	}

	@Override
	public float fetchFloat()
	{
		return next() ? getCurrentFloatValue() : 0;
	}

	@Override
	public double fetchDouble()
	{
		return next() ? getCurrentDoubleValue() : 0;
	}

	/**
	 * Gets the current column index to be read on the next read operation.
	 *
	 * @return the current column index to be read on the next read
	 * operation
	 */
	public int getCurrentColumnIndex()
	{
		return column;
	}

	/**
	 * Sets the column index to be read on the next read operation.
	 *
	 * @param index the column index to be read on the next read operation
	 */
	public void setCurrentColumnIndex(int index)
	{
		this.column = index;
	}

	/**
	 * Gets the {@link java.sql.ResultSet} associated with this Cursor.
	 *
	 * @return the {@link java.sql.ResultSet} associated with this Cursor
	 */
	public ResultSet getResultSet()
	{
		return rs;
	}

	/**
	 * Gets the {@link gate.sql.Command} that generated this Cursor.
	 *
	 * @return the {@link gate.sql.Command} that generated this Cursor
	 */
	public Command getCommand()
	{
		return command;
	}

	/**
	 * Checks if there is a next record of the query result to be read.
	 *
	 * @return true if there is a next record to be read and false otherwise
	 */
	public boolean isAfterLast()
	{
		try
		{
			return rs.isAfterLast();
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Move to the next record of the query result.
	 *
	 * @return true if there is a next record to be moved to, false
	 * otherwise
	 */
	public boolean next()
	{
		try
		{
			column = 1;
			return rs.next();
		} catch (SQLException e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	/**
	 * Gets the index of the current record being fetched by this cursor.
	 *
	 * @return the index of the current record being fetched by this cursor
	 */
	public int getCurrentRowIndex()
	{
		try
		{
			return rs.getRow();
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Checks if the Cursor is closed.
	 *
	 * @return true if the Cursor is closed and false otherwise
	 */
	public boolean isClosed()
	{
		try
		{
			return rs.isClosed();
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}

	}

	@Override
	public void close()
	{
		try
		{
			if (!rs.isClosed())
				rs.close();
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the current column value and moves the column index to the next
	 * column.
	 *
	 * @return the value of the current column
	 */
	public Object getCurrentColumnValue()
	{
		return Cursor.this.getValue(column++);
	}

	/**
	 * Reads the current column value as an object of the specified type and
	 * moves the column index to the next column.
	 *
	 *
	 * @param type type of the object to be read
	 *
	 * @return the value of the current column as an object of the specified
	 * type
	 */
	public <T> T getCurrentValue(Class<T> type)
	{
		try
		{
			Converter converter = Converter.getConverter(type);
			T value = (T) converter.readFromResultSet(getResultSet(), column, type);
			column += Math.max(1, converter.getSufixes().size());
			return value;
		} catch (ConversionException | SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value.
	 *
	 * @param columnIndex index of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public Object getValue(int columnIndex)
	{
		try
		{
			Class<?> type = SQLTypeConverter.getJavaType(rs.getMetaData().getColumnType(columnIndex));
			return Converter.getConverter(type).readFromResultSet(rs, columnIndex, type);
		} catch (ConversionException | SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java byte.
	 *
	 * @param columnIndex index of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public byte getByteValue(int columnIndex)
	{
		try
		{
			return rs.getByte(columnIndex);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the current specified column value as a java byte.
	 *
	 * @return the value of the specified column
	 */
	public byte getCurrentByteValue()
	{
		try
		{
			return rs.getByte(column++);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java short.
	 *
	 * @param columnIndex index of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public short getShortValue(int columnIndex)
	{
		try
		{
			return rs.getShort(columnIndex);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the current specified column value as a java short.
	 *
	 * @return the value of the specified column
	 */
	public short getCurrentShortValue()
	{
		try
		{
			return rs.getShort(column++);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java int.
	 *
	 * @param columnIndex index of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public int getIntValue(int columnIndex)
	{
		try
		{
			return rs.getInt(columnIndex);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the current specified column value as a java int.
	 *
	 * @return the value of the specified column
	 */
	public int getCurrentIntValue()
	{
		try
		{
			return rs.getInt(column++);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java long.
	 *
	 * @param columnIndex index of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public long getLongValue(int columnIndex)
	{
		try
		{
			return rs.getLong(columnIndex);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the current specified column value as a java long.
	 *
	 * @return the value of the specified column
	 */
	public long getCurrentLongValue()
	{
		try
		{
			return rs.getLong(column++);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java float.
	 *
	 * @param columnIndex index of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public float getFloatValue(int columnIndex)
	{
		try
		{
			return rs.getFloat(columnIndex);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the current specified column value as a java float.
	 *
	 * @return the value of the specified column
	 */
	public float getCurrentFloatValue()
	{
		try
		{
			return rs.getFloat(column++);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java double.
	 *
	 * @param columnIndex index of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public double getDoubleValue(int columnIndex)
	{
		try
		{
			return rs.getDouble(columnIndex);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the current specified column value as a java double.
	 *
	 * @return the value of the specified column
	 */
	public double getCurrentDoubleValue()
	{
		try
		{
			return rs.getDouble(column++);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java boolean.
	 *
	 * @param columnIndex index of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public boolean getBooleanValue(int columnIndex)
	{
		try
		{
			return rs.getBoolean(columnIndex);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the current specified column value as a java boolean.
	 *
	 * @return the value of the specified column
	 */
	public boolean getCurrentBooleanValue()
	{
		try
		{
			return rs.getBoolean(column++);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java char.
	 *
	 * @param columnIndex index of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public char getCharValue(int columnIndex)
	{
		try
		{
			return (char) rs.getInt(columnIndex);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the current specified column value as a java char.
	 *
	 * @return the value of the specified column
	 */
	public char getCurrentCharValue()
	{
		try
		{
			return (char) rs.getInt(column++);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as an object of the specified type.
	 *
	 *
	 * @param type type of the object to be read
	 * @param columnIndex index of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public <T> T getValue(Class<T> type, int columnIndex)
	{
		try
		{
			return (T) Converter.getConverter(type).readFromResultSet(getResultSet(), columnIndex, type);
		} catch (ConversionException | SQLException e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	/**
	 * Reads the specified column value as an object of the specified type.
	 *
	 *
	 * @param type type of the object to be read
	 * @param columnName name of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public <T> T getValue(Class<T> type, String columnName)
	{
		try
		{
			return (T) Converter.getConverter(type).readFromResultSet(getResultSet(), columnName, type);
		} catch (ConversionException | SQLException e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	/**
	 * Reads the specified column value as a java byte.
	 *
	 * @param columnName name of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public byte getByteValue(String columnName)
	{
		try
		{
			return rs.getByte(columnName);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java short.
	 *
	 * @param columnName name of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public short getShortValue(String columnName)
	{
		try
		{
			return rs.getShort(columnName);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java integer.
	 *
	 * @param columnName name of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public int getIntValue(String columnName)
	{
		try
		{
			return rs.getInt(columnName);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java long.
	 *
	 * @param columnName name of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public long getLongValue(String columnName)
	{
		try
		{
			return rs.getLong(columnName);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java float.
	 *
	 * @param columnName name of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public float getFloatValue(String columnName)
	{
		try
		{
			return rs.getFloat(columnName);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java double.
	 *
	 * @param columnName name of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public double getDoubleValue(String columnName)
	{
		try
		{
			return rs.getDouble(columnName);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the specified column value as a java boolean.
	 *
	 * @param columnName name of the column to be read
	 *
	 * @return the value of the specified column
	 */
	public boolean getBooleanValue(String columnName)
	{
		try
		{
			return rs.getBoolean(columnName);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Gets the number of columns of the cursor.
	 *
	 * @return the number of columns associated with this cursor
	 */
	public int getColumnCount()
	{
		try
		{
			return rs.getMetaData().getColumnCount();
		} catch (SQLException e)
		{
			throw new UnsupportedOperationException(e);
		}

	}

	/**
	 * Gets the names of columns of the cursor.
	 *
	 * @return a java array with the names of the columns associated with
	 * this cursor
	 */
	public String[] getColumnNames()
	{
		try
		{
			ResultSetMetaData rsmd = getResultSet().getMetaData();
			String[] names = new String[rsmd.getColumnCount()];
			for (int i = 0; i < names.length; i++)
				names[i] = rsmd.getColumnLabel(i + 1);
			return names;
		} catch (SQLException e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	/**
	 * Gets the default java types of the columns of the cursor.
	 *
	 * @return a java array with the default java types of the columns of
	 * the cursor
	 */
	public Class<?>[] getColumnTypes()
	{
		try
		{
			ResultSetMetaData rsmd = getResultSet().getMetaData();
			Class<?>[] types = new Class<?>[rsmd.getColumnCount()];
			for (int i = 0; i < types.length; i++)
				types[i] = SQLTypeConverter.getJavaType(rsmd.getColumnType(i + 1));
			return types;
		} catch (SQLException e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	public List<String> getPropertyNames(Class<?> type)
	{
		return Stream.of(getColumnNames())
			.map(e -> e.contains(Converter.SEPARATOR) ? e.split(Converter.SEPARATOR)[0] : e)
			.map(e -> e.contains("$") ? e.replaceAll("[$]", ".") : e)
			.distinct().collect(Collectors.toList());
	}

	public Map<String, Class<?>> getMetaData()
	{
		try
		{
			Map<String, Class<?>> result = new LinkedHashMap<>();
			ResultSetMetaData rsmd = getResultSet().getMetaData();
			int count = rsmd.getColumnCount();
			for (int i = 0; i < count; i++)
				result.put(rsmd.getColumnName(i + 1),
					SQLTypeConverter.getJavaType(rsmd.getColumnType(i + 1)));
			return result;
		} catch (SQLException e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	public List<Property> getProperties(Class<?> type)
	{
		return Stream.of(getColumnNames())
			.map(e -> e.contains(Converter.SEPARATOR) ? e.split("_")[0] : e)
			.map(e -> e.contains("$") ? e.replaceAll("[$]", ".") : e)
			.map(e -> Property.getProperty(type, e))
			.distinct()
			.collect(Collectors.toList());
	}

	/**
	 * Reads the current row as a java object of the specified type with the
	 * specified properties set to their respective column values.
	 *
	 *
	 * @param type type of the entity to be read
	 * @param properties entity properties to be read
	 *
	 * @return the current as a java object of the specified type with the
	 * specified properties set to their respective column values
	 */
	public <T> T getEntity(Class<T> type, List<Property> properties)
	{
		try
		{
			T result = type.getDeclaredConstructor().newInstance();
			properties.forEach(property ->
			{
				Class<?> clazz = property.getRawType();
				if (clazz == boolean.class)
					property.setBoolean(result, getCurrentBooleanValue());
				else if (clazz == char.class)
					property.setChar(result, getCurrentCharValue());
				else if (clazz == byte.class)
					property.setByte(result, getCurrentByteValue());
				else if (clazz == short.class)
					property.setShort(result, getCurrentShortValue());
				else if (clazz == int.class)
					property.setInt(result, getCurrentIntValue());
				else if (clazz == long.class)
					property.setLong(result, getCurrentLongValue());
				else if (clazz == float.class)
					property.setFloat(result, getCurrentFloatValue());
				else if (clazz == double.class)
					property.setDouble(result, getCurrentDoubleValue());
				else
					property.setValue(result, getCurrentValue(clazz));
			});
			return result;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
			| InvocationTargetException | NoSuchMethodException | SecurityException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	/**
	 * Reads the current row as a java object of the specified type with
	 * it's property values matched to their respective column values.
	 *
	 * @param type type of the entity to be read
	 *
	 * @return the current row as a java object of the specified type with
	 * it's property values matched to their respective column values.
	 */
	public <T> T getEntity(Class<T> type)
	{
		return getEntity(type, getProperties(type));
	}
}
