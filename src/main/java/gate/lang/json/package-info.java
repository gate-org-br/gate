/**
 * Provides utilities for serialization of java objects using JSON notation
 * and parsing strings on JSON notation to java objects.
 * <hr>
 * For each type of JSON element, an implementation of {@link gate.lang.json.JsonElement} is provided. Each one of them
 * has a parse and a format static method:
 * <ul>
 * <li>{@link gate.lang.json.JsonBoolean}:
 * <pre>{@code
 *	JsonBoolean jsonBoolean = JsonBoolean.parse("true");
 *	String json = JsonBoolean.format(jsonBoolean);
 * }</pre>
 * <li> {@link gate.lang.json.JsonNumber}:
 * <pre>{@code
 * 	JsonNumber jsonNumber = JsonNumber.parse("12.48");
 *	String json = JsonNumber.format(jsonNumber);
 * }</pre>
 * <li>{@link gate.lang.json.JsonString}:
 * <pre>{@code
 * 	JsonString jsonString = JsonString.parse("'this is a quoted string'");
 *	String json = javaString.format(jsonString);
 * }</pre>
 * <li>{@link gate.lang.json.JsonArray}:
 * <pre>{@code
 * 	JsonArray jsonArray = JsonArray.parse("[1, 2, 3, true, false]");
 *	String json = JsonArray.format(jsonArray);
 * }</pre>
 * <li>{@link gate.lang.json.JsonObject}:
 * <pre>{@code
 * 	JsonObject jsonObject = JsonObject.parse("{'field1':'value1'}");
 *	String json = JsonArray.format(jsonObject);
 * }</pre>
 * <li>{@link gate.lang.json.JsonNull}:
 * <pre>{@code
 * 	JsonNull jsonNull = JsonObject.parse("null");
 *	String json = JsonArray.format(jsonNull);
 * }</pre>
 * </ul>
 * <hr>
 * The {@link gate.lang.json.JsonElement} interface provides static format and parse methods to be used when the type of
 * the element to be parsed or formatted is unknown:
 * <pre>{@code
 *	JsonElement jsonElement = JsonElement.parse(unkownString);
 *	switch(jsonElement.getType())
 *	{
 *		case BOOLEAN:
 *			JsonBoolean jsonBoolean = (JsonBoolean)jsonElement;
 *			break;
 *		case STRING:
 *			JsonString jsonString = (JsonString)jsonElement;
 *			break;
 *	}
 *	String json = JsonElement.format(jsonElement);
 * }</pre>
 * <hr>
 * A {@link gate.lang.json.JsonArray} is a List of JsonElement:
 * <pre>{@code
 * 	JsonArray jsonArray = JsonArray
 *		.parse("[1, 2, 3, true, false, [1, 2]]");
 *	for (JsonElement jsonElement : jsonArray)
 *		switch(jsonElement.getType())
 *		{
 *			case ARRAY:
 *				JsonArray innerArray
 *					= (JsonArray)jsonElement;
 *				break;
 *			case STRING:
 *				JsonString jsonString
 *					= (JsonString)jsonElement;
 *				break;
 *		}
 * }</pre>
 * <hr>
 * A {@link gate.lang.json.JsonObject} is a Map of String to JsonElement:
 * <pre>{@code
 * 	JsonObject jsonObject = JsonObject
 *		.parse("{'string': 'value1', 'number': 1, 'array': [1, 2, 3]}");
 *
 *	JsonString string = (JsonString)jsonObject.get('string');
 *	JsonNumber number = (JsonNumber)jsonObject.get('number');
 *	JsonArray array = (JsonArray)jsonObject.get('array');
 *
 * }</pre>
 * <hr>
 * The {@link gate.converter.Converter} interface provides static format and parse methods which can be used to format
 * and parse generic java objects:
 * <pre>{@code
 *	User user = new User();
 *	user.setName("Jonh");
 *	user.getRole().setName("Admin");
 *	String json = Converter.javaToJson(user);
 *	User jonh = Converter.jsonToJava(User.class, json);
 * }</pre>
 * <hr>
 * The {@link gate.lang.json.JsonParser} provides a way to read {@link gate.lang.json.JsonElement} objects sequentially
 * from a {@link java.io.Reader}:
 * <pre>{@code
 *	String jsonSequence = "{'field1': 'value1'} [1, 2, 3] true 'string'";
 *	try (JsonParser parser = new JsonParser(new StringReader(jsonSequence)))
 *	{
 *		for (Optional<JsonElement> optional = parser.parse();
 *			optional.isPresent(); optional = parser.parse())
 *		System.out.print(optional.get().toString());
 *	}
 *
 *	try (JsonParser parser = new JsonParser(new StringReader(jsonSequence)))
 *	{
 *		for (JsonElement element : parser)
 *			System.out.print(optional.get().toString());
 *	}
 *
 *	try (JsonParser parser = new JsonParser(new StringReader(jsonSequence)))
 *	{
 *		parser.stream().map(JsonElement::getType)
 *			forEach(System.out::print);
 *	}
 * }</pre>
 */
package gate.lang.json;
