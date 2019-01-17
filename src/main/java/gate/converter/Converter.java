package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import javax.servlet.http.Part;

/**
 * Adapts a java class to be used by the gate framework.
 */
public interface Converter
{

	default List<String> getSufixes()
	{
		return Collections.emptyList();
	}

	default Stream<String> getColumns(String column)
	{
		return getSufixes().isEmpty() ? Stream.of(column)
			: getSufixes().stream().map(e -> column + "$" + e);
	}

	/**
	 * Gets a string to be used as a mask for the converter java type.
	 *
	 * @return the mask for the converter java type or null if there is no mask defined for it
	 */
	String getMask();

	/**
	 * Gets a description for the converter java type.
	 *
	 * @return a description for the converter java type or null if there is no description defined for it
	 */
	String getDescription();

	/**
	 * Gets a placeholder for the converter java type.
	 *
	 * @return a placeholder for the converter java type or null if there is no placeholder defined for it
	 */
	default String getPlaceholder()
	{
		return null;
	}

	/**
	 * Gets the constraints associated with the converter java type.
	 *
	 * @return a list with all the constraints associated with the converter java type
	 */
	List<Constraint.Implementation<?>> getConstraints();

	/**
	 * Retrieves a java object of the converter associated type from a JDBC ResultSet.
	 *
	 * @param rs the JDBC ResultSet from where the java object must be retrieved
	 * @param index the start index of the columns associated with the java object to be retrieved
	 * @param type the type for the java object to be retrieved
	 *
	 * @return the java object retrieved from the specified JDBC ResultSet
	 *
	 * @throws java.sql.SQLException if a SQLException exception is thrown while retrieving the object from the JDBC ResultSet
	 * @throws gate.error.ConversionException if the specified type can't be retrieved from a JDBC ResultSet
	 *
	 * @see gate.converter.Converter#writeToPreparedStatement
	 */
	Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException,
		ConversionException;

	/**
	 * Retrieves a java object from a JDBC ResultSet.
	 *
	 * @param rs the JDBC ResultSet from where the java object must be retrieved
	 * @param fields the name of the columns associated with the java object to be retrieved
	 * @param type the type for the java object to be retrieved
	 *
	 * @return the java object retrieved from the specified JDBC ResultSet
	 *
	 * @throws java.sql.SQLException if a SQLException exception is thrown while retrieving the object from the JDBC ResultSet
	 * @throws gate.error.ConversionException if the specified type can't be retrieved from a JDBC ResultSet
	 *
	 * @see gate.converter.Converter#writeToPreparedStatement
	 */
	Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException,
		ConversionException;

	/**
	 * Passes the specified object as parameters to a JDBC PreparedStatement.
	 *
	 * @param ps the JDBC PreparedStatement to receive the specified object as parameters
	 * @param index the starting index of the parameters to be passed
	 * @param value the object to be passed as parameters to the JDBC PreparedStatement
	 *
	 * @return the index to be used on the next call to this method
	 *
	 * @throws java.sql.SQLException if a SQLException exception is thrown while setting the parameters
	 * @throws gate.error.ConversionException if the specified value can't be set as parameters of a JDBC PreparedStatement
	 */
	int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException,
		ConversionException;

	/**
	 * Converts the specified java object to a {@link java.math.Number}.
	 *
	 * @param type type of the object to be converted to a Number
	 * @param object object to be converted to a Number
	 *
	 * @return the specified object as a Number
	 */
	default Number toNumber(Class<?> type, Object object)
	{
		throw new UnsupportedOperationException(String.format("Objects of type %s can't be converted to %s.",
			type.getName(), Number.class.getName()));
	}

	/**
	 * Converts the specified java object to a string.
	 * <p>
	 * The object to be converted must be of the converter associated java type.
	 * <p>
	 * The string generated by this method must have all information necessary for it to be converted back to the original object.
	 *
	 * @param type type of the object to be converted to a string
	 * @param object object to be converted to a string
	 *
	 * @return the specified object as a java string
	 *
	 * @see gate.converter.Converter#ofString
	 */
	String toString(Class<?> type, Object object);

	/**
	 * Converts the specified string back to the it's original java object.
	 *
	 * @param type type of the object that originated the specified string
	 * @param string the string to be converted back to it's original java object
	 *
	 * @return the object that was previously converted to the specified string
	 *
	 * @throws gate.error.ConversionException if the specified string can't be converted to it's original java object
	 *
	 * @see gate.converter.Converter#toString
	 */
	Object ofString(Class<?> type, String string) throws ConversionException;

	/**
	 * <p>
	 * Converts the specified java object to a string.
	 * <p>
	 * The object to be converted must be of the converter associated java type.
	 * <p>
	 * The string generated by this method is intended to be read by humans and there is no need for it to have enough information to restore the original
	 * object.
	 *
	 * @param type type of the object to be converted to a string
	 * @param object object to be converted to a string
	 *
	 * @return the specified object as a java string
	 */
	String toText(Class<?> type, Object object);

	/**
	 * Converts the specified java object to a string.
	 * <p>
	 * The object to be converted must be of the converter associated java type.
	 * <p>
	 * The string generated by this method is intended to be read by humans and there is no need for it to have enough information to restore the original
	 * object.
	 *
	 * @param type type of the object to be converted to a string
	 * @param object object to be converted to a string
	 * @param format an string to be used to format the specified object
	 *
	 * @return the specified object as a java string
	 */
	String toText(Class<?> type, Object object, String format);

	/**
	 * Converts the specified part to a java object.
	 *
	 * @param type type of the object to what the part must be converted
	 * @param part the part to be converted to a java object
	 *
	 * @return a java object describing the specified part
	 *
	 * @throws gate.error.ConversionException if the specified part can't be converted to a java object
	 */
	default Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		throw new UnsupportedOperationException("This type can't be converted from a Part.");
	}

	/**
	 * Reads an object of the converter associated type from the specified {@link gate.lang.json.JsonScanner}.
	 *
	 * @param jsonScanner scanner from where the JSON object must be read
	 * @param type type of the enumeration or collection element to be read
	 * @param elementType type of the element of the object if the object is a collection or null if not
	 *
	 * @return the JSON object read as a java object of the converter associated type
	 *
	 * @throws gate.error.ConversionException if the next element of the specified JSONReader cannot be converted to a java object of the converter
	 * associated type.
	 */
	default Object ofJson(JsonScanner jsonScanner, Type type, Type elementType)
		throws ConversionException
	{
		switch (jsonScanner.getCurrent().getType())
		{
			case NULL:
				jsonScanner.scan();
				return null;
			case STRING:
				Object value = Converter
					.fromString((Class<?>) type,
						jsonScanner.getCurrent().toString());
				jsonScanner.scan();
				return value;
			default:
				throw new ConversionException(jsonScanner.getCurrent() + " is not a " + type.getTypeName());
		}
	}

	/**
	 * Serializes the specified object on JSON notation.
	 *
	 * @param <T> type of the java object to be serialized
	 * @param writer JsonWriter responsible for serializing the object
	 * @param type type of the java object to be serialized
	 * @param object object to be serialized
	 *
	 * @throws ConversionException if the specified object cannot be serialized on JSON notation
	 */
	default <T> void toJson(JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		if (object != null)
			writer.write(JsonToken.Type.STRING, toString(object));
	}

	/**
	 * Gets the converter associated with the specified java class.
	 *
	 * @param type java class whose associated converted must be returned
	 *
	 * @return the converter associated with the specified java class
	 */
	static Converter getConverter(Class<?> type)
	{
		return Converters.INSTANCE.get(type);
	}

	/**
	 * Converts the specified java object to a string.
	 * <p>
	 * The string generated by this method have all information necessary for it to be converted back to the original object.
	 *
	 * @param object object to be converted to a string
	 *
	 * @return the specified object as a java string
	 *
	 * @see gate.converter.Converter#stringToObject
	 */
	static String toString(Object object)
	{
		if (object == null)
			return "";
		Class<?> type = object.getClass();
		return getConverter(type).toString(type, object);
	}

	/**
	 * Converts the specified java object to a string.
	 * <p>
	 * The string generated by this method is intended to be read by humans and there is no need for it to have enough information to restore the original
	 * object.
	 *
	 * @param object object to be converted to a string
	 *
	 * @return the specified object as a java string
	 */
	static String toText(Object object)
	{
		if (object == null)
			return "";
		Class<?> type = object.getClass();
		return getConverter(type).toText(type, object);
	}

	/**
	 * Converts the specified java object to a string.
	 * <p>
	 * The string generated by this method is intended to be read by humans and there is no need for it to have enough information to restore the original
	 * object.
	 *
	 * @param object object to be converted to a string
	 * @param format an string to be used to format the specified object
	 *
	 * @return the specified object as a java string
	 */
	static String toText(Object object, String format)
	{
		if (object == null)
			return "";

		Class<?> type = object.getClass();
		Converter converter = getConverter(type);

		if (format == null)
			return converter.toText(type, object);
		return converter.toText(type, object, format);
	}

	/**
	 * Serializes the specified java object on JSON notation.
	 * <p>
	 * The specified object will be serialized as described on the specified java type associated {@link gate.converter.Converter}.
	 *
	 * @param object the java object to be serialized
	 *
	 * @return the specified java object serialized using JSON notation
	 * @throws ConversionException if the specified object can't be serialized on JSON notation
	 *
	 * @see gate.converter.BooleanConverter#toJson
	 * @see gate.converter.NumberConverter#toJson
	 * @see gate.converter.StringConverter#toJson
	 * @see gate.converter.CollectionConverter#toJson
	 * @see gate.converter.ObjectConverter#toJson
	 */
	static String toJson(Object object)
		throws ConversionException
	{
		if (object == null)
			return "null";

		try (StringWriter stringWriter = new StringWriter();
			JsonWriter writer = new JsonWriter(stringWriter))
		{
			Class type = object.getClass();
			Converter converter = Converter.getConverter(type);
			converter.toJson(writer, type, object);
			return stringWriter.toString();
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage());
		}
	}

	/**
	 * Converts the specified java object to a {@link java.math.Number}.
	 *
	 * @param object object to be converted to a Number
	 *
	 * @return the specified object as a Number
	 */
	static Number toNumber(Object object)
	{
		return object != null
			? getConverter(object.getClass()).toNumber(object.getClass(), object)
			: null;
	}

	/**
	 * Parses the specified JSON formatted string into a java object of the specified type.
	 * <p>
	 * The JSON formatted string will be parsed as described on the specified java type associated {@link gate.converter.Converter}.
	 *
	 * @param <T> type of the java object to be created
	 * @param type type of the java object to be created
	 * @param elementType type of the element of the specified object if it is a collection or null if otherwise
	 * @param string JSON formatted string to be parsed
	 *
	 * @return a java object of the specified type representing the specified JSON formatted string
	 *
	 * @throws ConversionException if the specified JSON string cannot be parsed into a object of the specified java type
	 *
	 * @see gate.converter.BooleanConverter#ofJson
	 * @see gate.converter.NumberConverter#ofJson
	 * @see gate.converter.StringConverter#ofJson
	 * @see gate.converter.CollectionConverter#ofJson
	 * @see gate.converter.ObjectConverter#ofJson
	 */
	static <T> T fromJson(Class<T> type, Type elementType, String string) throws ConversionException
	{
		if (string == null)
			return null;

		string = string.trim();
		if (string.isEmpty())
			return null;

		try (JsonScanner scanner = new JsonScanner(new StringReader(string)))
		{
			Converter converter = Converter.getConverter(type);
			return type.cast(converter.ofJson(scanner, type, elementType));
		}
	}

	/**
	 * Parses the specified JSON formatted string into a java object of the specified type.
	 * <p>
	 * The JSON formatted string will be parsed as described on the specified java type associated {@link gate.converter.Converter}.
	 *
	 * @param <T> type of the java object to be created
	 * @param type type of the java object to be created
	 * @param string JSON formatted string to be parsed
	 *
	 * @return a java object of the specified type representing the specified JSON formatted string
	 *
	 * @throws ConversionException if the specified JSON string cannot be parsed into a object of the specified java type
	 *
	 * @see gate.converter.BooleanConverter#ofJson
	 * @see gate.converter.NumberConverter#ofJson
	 * @see gate.converter.StringConverter#ofJson
	 * @see gate.converter.CollectionConverter#ofJson
	 * @see gate.converter.ObjectConverter#ofJson
	 */
	static <T> T fromJson(Class<T> type, String string) throws ConversionException
	{
		return Converter.fromJson(type, null, string);
	}

	/**
	 * Converts the specified string back to the it's original java object.
	 *
	 * @param <T> type of the object that originated the specified string
	 * @param type type of the object that originated the specified string
	 * @param string the string to be converted back to it's original java object
	 *
	 * @return the object that was previously converted to the specified string
	 *
	 * @throws gate.error.ConversionException if the specified string can't be converted to it's original java object
	 *
	 * @see gate.converter.Converter#toString
	 */
	static <T> T fromString(Class<T> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		return type.cast(Converter.getConverter(type)
			.ofString(type, string));
	}

	/**
	 * Converts the specified part to a java object.
	 *
	 * @param <T> type of the object to what the part must be converted
	 * @param type type of the object to what the part must be converted
	 * @param part the part to be converted to a java object
	 *
	 * @return a java object describing the specified part
	 *
	 * @throws gate.error.ConversionException if the specified part can't be converted to a java object
	 */
	static <T> T fromPart(Class<T> type, Part part) throws ConversionException
	{
		if (part == null)
			return null;

		return type.cast(Converter.getConverter(type).ofPart(type, part));
	}
}
