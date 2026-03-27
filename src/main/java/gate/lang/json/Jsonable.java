package gate.lang.json;

/**
 * Defines a structural JSON representation for an object.
 * <p>
 * Implementations of this interface can provide their own {@link JsonElement}
 * representation when consumed by {@link JsonElement#of(Object)} and related
 * JSON tree-building APIs.
 * <p>
 * This contract is limited to producing {@link JsonElement} values and does
 * not affect JSON string serialization.
 */
public interface Jsonable
{
	/**
	 * Returns the JSON representation of this object.
	 *
	 * @return the JSON representation of this object
	 */
	JsonElement toJson();

	/**
	 * Returns a human-oriented JSON representation of this object.
	 * <p>
	 * The returned value is still a {@link JsonElement}, but it may favor
	 * readability over faithful reconstruction of the original object.
	 * <p>
	 * The default implementation reuses {@link #toJson()}.
	 *
	 * @return a text-oriented JSON representation of this object
	 */
	default JsonElement toJsonText() {return toJson();}
}
