package gate.lang.json;

/**
 * Adapts a java type to a structural {@link JsonElement} representation.
 * <p>
 * Implementations of this interface can provide custom JSON tree conversion
 * when consumed by {@link JsonElement#of(Object)} and related JSON
 * tree-building APIs.
 * <p>
 * This contract is limited to conversion between java objects and
 * {@link JsonElement} values. It does not define JSON text serialization.
 *
 * @param <T> java type adapted by this interface
 */
public interface JsonAdapter<T>
{
	/**
	 * Gets the {@link JsonAdapter} associated with the specified java type.
	 *
	 * @param type java type whose associated adapter must be returned
	 * @param <T>  java type adapted by the returned adapter
	 * @return the adapter associated with the specified type or {@code null}
	 * if no adapter is registered or annotated for it
	 */
	static <T> JsonAdapter<T> of(Class<T> type)
	{
		return JsonAdapters.INSTANCE.get(type);
	}

	/**
	 * Registers the specified adapter for the specified java type.
	 * <p>
	 * Registered adapters take precedence over annotation-based resolution.
	 *
	 * @param type    java type associated with the adapter
	 * @param adapter adapter to be associated with the specified type
	 * @param <T>     java type adapted by the specified adapter
	 */
	static <T> void register(Class<T> type, JsonAdapter<? super T> adapter)
	{
		JsonAdapters.INSTANCE.register(type, adapter);
	}

	/**
	 * Converts the specified java object to its JSON representation.
	 * <p>
	 * The default contract accepts {@code null} values and should return
	 * {@code null} for them.
	 *
	 * @param object java object to be converted
	 * @return the structural JSON representation of the specified object
	 */
	JsonElement toJson(T object);

	/**
	 * Converts the specified {@link JsonElement} back to its java
	 * representation.
	 * <p>
	 * The default contract accepts {@code null} values and should return
	 * {@code null} for them.
	 *
	 * @param json JSON element to be converted
	 * @return the java object represented by the specified JSON element
	 */
	T fromJson(JsonElement json);

	/**
	 * Converts the specified java object to a human-oriented JSON
	 * representation.
	 * <p>
	 * The returned value is still a {@link JsonElement}, but it may favor
	 * readability over faithful reconstruction of the original object.
	 * <p>
	 * The default implementation reuses {@link #toJson(Object)}.
	 * <p>
	 * The default contract accepts {@code null} values and should return
	 * {@code null} for them.
	 *
	 * @param object java object to be converted
	 * @return a text-oriented JSON representation of the specified object
	 */
	default JsonElement toJsonText(T object) {return toJson(object);}
}