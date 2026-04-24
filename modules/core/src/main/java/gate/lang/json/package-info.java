/**
 * Provides JSON parsing, serialization, structural wrapping, typed
 * encoding/decoding, and display-oriented rendering utilities.
 * <hr>
 * Core operations exposed by {@link gate.lang.json.JsonElement}:
 * <ul>
 * <li>{@link gate.lang.json.JsonElement#parse(String)} and
 * {@link gate.lang.json.JsonElement#stringify(gate.lang.json.JsonElement)}
 * for JSON text</li>
 * <li>{@link gate.lang.json.JsonElement#wrap(Object)} and
 * {@link gate.lang.json.JsonElement#unwrap()} for natural Java structural
 * values</li>
 * <li>{@link gate.lang.json.JsonElement#encode(Object)} and
 * {@link gate.lang.json.JsonElement#decode(Class)} for typed object
 * conversion</li>
 * <li>{@link gate.lang.json.JsonElement#render(Object)} for human-oriented
 * display rendering</li>
 * </ul>
 * <hr>
 * Example:
 * <pre>{@code
 * 	JsonElement json = JsonElement.parse("{\"name\":\"Gate\",\"active\":true}");
 * 	String string = JsonElement.stringify(json);
 * 	Object value = json.unwrap();
 *
 * 	JsonElement wrapped = JsonElement.wrap(Map.of("name", "Gate"));
 * 	JsonElement encoded = JsonElement.encode(new User());
 * 	JsonElement rendered = JsonElement.render(true);
 * }</pre>
 * <hr>
 * A {@link gate.lang.json.JsonArray} is a {@link java.util.List} of
 * {@link gate.lang.json.JsonElement} and a
 * {@link gate.lang.json.JsonObject} is a {@link java.util.Map} from
 * {@link java.lang.String} to {@link gate.lang.json.JsonElement}.
 * <hr>
 * The {@link gate.lang.json.JsonParser} provides a way to read
 * {@link gate.lang.json.JsonElement} values sequentially from a
 * {@link java.io.Reader}.
 */
package gate.lang.json;
