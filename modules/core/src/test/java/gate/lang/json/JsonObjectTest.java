package gate.lang.json;

import gate.error.ConversionException;
import mock.ContactMock;
import mock.IDMock;
import mock.MockFactory;
import mock.UserMock;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonObjectTest
{

	@Test
	public void shouldSetAndGetTypedValuesByKey()
	{
		JsonObject object = new JsonObject()
				.setString("string", "value")
				.setBoolean("boolean", true)
				.setByte("byte", (byte) 1)
				.setShort("short", (short) 2)
				.setInt("int", 3)
				.setLong("long", 4L)
				.setFloat("float", 5.5f)
				.setDouble("double", 6.5d);

		assertEquals("value", object.getString("string").orElseThrow());
		assertEquals(true, object.getBoolean("boolean").orElseThrow());
		assertEquals((byte) 1, object.getByte("byte").orElseThrow());
		assertEquals((short) 2, object.getShort("short").orElseThrow());
		assertEquals(3, object.getInt("int").orElseThrow());
		assertEquals(4L, object.getLong("long").orElseThrow());
		assertEquals(5.5f, object.getFloat("float").orElseThrow());
		assertEquals(6.5d, object.getDouble("double").orElseThrow());
	}

	@Test
	public void shouldGetTypedValuesByInsertionIndex()
	{
		JsonObject object = new JsonObject()
				.setString("string", "value")
				.setInt("int", 3)
				.setBoolean("boolean", true)
				.set("object", new JsonObject().setString("name", "nested"))
				.set("array", JsonArray.of(JsonString.of("item")));

		assertEquals("value", object.getString(0).orElseThrow());
		assertEquals(3, object.getInt(1).orElseThrow());
		assertEquals(true, object.getBoolean(2).orElseThrow());
		assertEquals("nested", object.getJsonObject(3).orElseThrow().getString("name").orElseThrow());
		assertEquals("item", object.getJsonArray(4).orElseThrow().getString(0).orElseThrow());
		assertTrue(object.getString(99).isEmpty());
	}

	@Test
	public void shouldReturnEmptyOptionalWhenTypeDoesNotMatchOrKeyIsMissing()
	{
		JsonObject object = new JsonObject()
				.setString("string", "value")
				.setInt("int", 3)
				.setBoolean("boolean", true);

		assertTrue(object.getInt("string").isEmpty());
		assertTrue(object.getString("int").isEmpty());
		assertTrue(object.getJsonBoolean("string").isEmpty());
		assertTrue(object.getJsonElement("missing").isEmpty());
	}

	@Test
	public void shouldRemoveValuesWhenSettingNull()
	{
		JsonObject object = new JsonObject()
				.setString("string", "value")
				.setBoolean("boolean", true)
				.setByte("byte", (Byte) (byte) 1)
				.setShort("short", (Short) (short) 2)
				.setInt("int", (Integer) 3)
				.setLong("long", (Long) 4L)
				.setFloat("float", (Float) 5.5f)
				.setDouble("double", (Double) 6.5d)
				.setObject("object", IDMock.valueOf(1));

		object.setString("string", null)
				.setBoolean("boolean", null)
				.setByte("byte", null)
				.setShort("short", null)
				.setInt("int", null)
				.setLong("long", null)
				.setFloat("float", null)
				.setDouble("double", null)
				.setObject("object", null)
				.set("missing", null);

		assertTrue(object.isEmpty());
	}

	@Test
	public void shouldConvertObjectsThroughConverterStrings()
	{
		JsonObject object = new JsonObject()
				.setObject("id", IDMock.valueOf(10))
				.setObject("typed", IDMock.class, IDMock.valueOf(11));

		assertEquals(JsonString.of("10"), object.get("id"));
		assertEquals(IDMock.valueOf(10), object.getObject("id", IDMock.class).orElseThrow());
		assertEquals(IDMock.valueOf(11), object.getObject("typed", IDMock.class).orElseThrow());
		assertEquals(IDMock.valueOf(10), object.getObject(0, IDMock.class).orElseThrow());
	}

	@Test
	public void shouldParseStringifyAndPreserveNestedStructure()
	{
		JsonObject object = new JsonObject()
				.setString("name", "User 1")
				.setInt("id", 1)
				.setBoolean("active", true)
				.set("contacts", JsonArray.of(
						new JsonObject()
								.setString("type", "EMAIL")
								.setString("value", "user1@example.com")));

		String json = JsonElement.stringify(object);

		assertEquals(object, JsonObject.parse(json));
		assertEquals(object, JsonObject.valueOf(json));
		assertEquals(json, object.toString());
	}

	@Test
	public void shouldRejectInvalidOrNonObjectJson()
	{
		assertThrows(ConversionException.class,
				() -> JsonObject.parse("{ \"active\": true, \"name\": \"John\""));
		assertThrows(ConversionException.class, () -> JsonObject.parse("[1, 2, 3]"));
		assertThrows(NullPointerException.class, () -> JsonObject.parse(null));
	}

	@Test
	public void shouldParseJsonObjectWithWhitespace()
	{
		JsonObject object = JsonObject.parse(
				"{ \"active\": \r\n\t true, \"name\" :\n\t \"John\" }");

		assertEquals(JsonBoolean.TRUE, object.get("active"));
		assertEquals(JsonString.of("John"), object.get("name"));
	}

	@Test
	public void shouldWrapAndUnwrapNaturalJavaMap()
	{
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name", "User 1");
		map.put("age", 30);
		map.put("active", true);
		map.put("tags", List.of("admin", "user"));
		map.put("address", Map.of("city", "Salvador"));
		map.put("null", null);

		JsonObject object = JsonObject.wrap(map);

		assertEquals("User 1", object.getString("name").orElseThrow());
		assertEquals(30, object.getInt("age").orElseThrow());
		assertEquals(true, object.getBoolean("active").orElseThrow());
		assertEquals("admin", object.getJsonArray("tags").orElseThrow().getString(0).orElseThrow());
		assertEquals("Salvador", object.getJsonObject("address").orElseThrow().getString("city").orElseThrow());
		assertSame(JsonNull.INSTANCE, object.get("null"));

		Map<String, Object> result = object.unwrap();
		assertEquals("User 1", result.get("name"));
		assertEquals(true, result.get("active"));
		assertNull(result.get("null"));
		assertEquals(List.of("admin", "user"), result.get("tags"));
		assertEquals(Map.of("city", "Salvador"), result.get("address"));
	}

	@Test
	public void shouldRejectMapWithNonStringKeyWhenWrapping()
	{
		assertThrows(ConversionException.class,
				() -> JsonElement.wrap(Map.of(1, "value")));
	}

	@Test
	public void shouldResolvePathsWithoutThrowing()
	{
		JsonObject object = new JsonObject()
				.setString("name", "User 1")
				.setInt("level", 10);

		assertEquals(JsonString.of("User 1"), object.path("name"));
		assertEquals(JsonString.of("User 1"), object.path(0));
		assertEquals(JsonNumber.of(10), object.path(1));
		assertEquals(JsonNumber.of(10), object.path(-1));
		assertEquals(JsonString.of("User 1"), object.path(-2));
		assertSame(JsonNull.INSTANCE, object.path("missing"));
		assertSame(JsonNull.INSTANCE, object.path(2));
		assertSame(JsonNull.INSTANCE, object.path(-3));
		assertNull(object.path(null));
	}

	@Test
	public void shouldCreateEntriesAndRenderObjectsWithLabelAndValue()
	{
		UserMock user = MockFactory.user(1);

		JsonObject entry = JsonObject.entries(user, UserMock::getName, UserMock::getId);
		JsonObject rendered = JsonObject.render(user, UserMock::getName, UserMock::getLevel);
		JsonObject withProperties = JsonObject.entries(user, UserMock::getName, UserMock::getId,
				value -> new JsonObject().setString("role", value.getRole().getName()));

		assertEquals("User 1", entry.getString("label").orElseThrow());
		assertEquals(1, entry.getInt("value").orElseThrow());
		assertEquals("User 1", rendered.getString("label").orElseThrow());
		assertEquals(10, rendered.getInt("value").orElseThrow());
		assertEquals("Role", withProperties.getJsonObject("properties").orElseThrow()
				.getString("role").orElseThrow());
	}

	@Test
	public void shouldDecodeJsonObjectIntoJavaMock()
	{
		JsonObject object = new JsonObject()
				.setInt("id", 1)
				.setString("name", "User 1")
				.setInt("level", 10)
				.set("role", new JsonObject()
						.setString("id", "2")
						.setString("name", "Role 2"))
				.set("contacts", JsonArray.of(new JsonObject()
						.setInt("id", 3)
						.setString("type", "EMAIL")
						.setString("value", "user1@example.com")));

		UserMock user = object.decode(UserMock.class);

		assertEquals(1, user.getId());
		assertEquals("User 1", user.getName());
		assertEquals(10, user.getLevel());
		assertEquals(IDMock.valueOf(2), user.getRole().getId());
		assertEquals("Role 2", user.getRole().getName());
		assertEquals(1, user.getContacts().size());
		assertEquals(3, user.getContacts().get(0).getId());
		assertEquals(ContactMock.Type.EMAIL, user.getContacts().get(0).getType());
		assertEquals("user1@example.com", user.getContacts().get(0).getValue());
	}

	@Test
	public void shouldExposeMapOperations()
	{
		JsonObject object = new JsonObject(Map.of("name", JsonString.of("User 1")));

		assertEquals(1, object.size());
		assertFalse(object.isEmpty());
		assertTrue(object.containsKey("name"));
		assertTrue(object.containsValue(JsonString.of("User 1")));
		assertEquals(JsonString.of("User 1"), object.get("name"));

		object.put("id", JsonNumber.of(1));
		object.put("active", JsonBoolean.TRUE);

		assertTrue(object.keySet().containsAll(List.of("name", "id", "active")));
		assertEquals(3, object.size());
		assertEquals(3, object.size());
		assertEquals(JsonNumber.of(1), object.remove("id"));
	}
}