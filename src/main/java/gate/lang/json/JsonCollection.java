package gate.lang.json;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a JSON collection that can be either a JSON object or array.
 */
public interface JsonCollection extends JsonElement
{

	/**
	 * Retrieves a nested property value from the JSON structure using a property path.
	 *
	 * The path can use dot notation for object properties and bracket notation for array indices or quoted property
	 * names.
	 *
	 * <p>
	 * This method traverses the JSON structure following the provided property path and returns an Optional containing
	 * the found element or empty if the property doesn't exist.
	 * </p>
	 *
	 * <p>
	 * The path can use dot notation for object properties and bracket notation for array indices or quoted property
	 * names.
	 * </p>
	 *
	 * @param property the property path to access
	 * @return an Optional containing the JsonElement if found, or empty if the property doesn't exist
	 * @throws IllegalArgumentException if the property path syntax is invalid or if attempting to access an array index
	 * on a non-array element, access an object property on a non-object element or use invalid property path syntax
	 */
	public default Optional<JsonElement> getProperty(String property)
	{
		Pattern pattern = Pattern.compile("(?:^|\\.)([_$a-zA-Z][_$a-zA-Z0-9]*)|(?:\\[)(\\d+|([\"'])((?:(?!\\3).)*?)\\3)(?:\\])|(.+)");
		Matcher matcher = pattern.matcher(property);

		JsonElement element = this;

		while (matcher.find())
		{
			if (matcher.group(5) != null)
				throw new IllegalArgumentException(property + " is no a valid property");

			Object token;
			if (matcher.group(1) != null)
				token = matcher.group(1);
			else if (matcher.group(4) != null)
				token = matcher.group(4);
			else
				token = Integer.valueOf(matcher.group(2));

			if (token instanceof String string)
				if (element instanceof JsonObject jsonObject)
					element = jsonObject.get(string);
				else
					throw new IllegalArgumentException(property + " is not a valid property");
			else if (token instanceof Integer integer)
				if (element instanceof JsonArray jsonArray)
					element = jsonArray.get(integer);
				else
					throw new IllegalArgumentException(property + " is not a valid property");

			if (element == null)
				return Optional.empty();
		}

		return Optional.of(element);
	}

}
