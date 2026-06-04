package gate.lang.json;

import gate.error.ConversionException;
import mock.MockFactory;
import mock.UserMock;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonElementTest
{

	@Test
	public void shouldIdentifyJsonTypesFromJavaValues()
	{
		assertEquals(JsonElement.Type.NULL, JsonElement.Type.of(null));
		assertEquals(JsonElement.Type.STRING, JsonElement.Type.of("value"));
		assertEquals(JsonElement.Type.NUMBER, JsonElement.Type.of(1));
		assertEquals(JsonElement.Type.BOOLEAN, JsonElement.Type.of(true));
		assertEquals(JsonElement.Type.ARRAY, JsonElement.Type.of(List.of(1)));
		assertEquals(JsonElement.Type.ARRAY, JsonElement.Type.of(new String[]{"a"}));
		assertEquals(JsonElement.Type.OBJECT, JsonElement.Type.of(Map.of("name", "Gate")));
		assertEquals(JsonElement.Type.OBJECT, JsonElement.Type.of(new JsonObject()));
		assertEquals(JsonElement.Type.NUMBER, JsonElement.Type.of(JsonElement.Type.NUMBER));
	}

	@Test
	public void shouldParseStringifyAndValueOfJsonElements()
	{
		JsonElement object = JsonElement.parse("{\"name\":\"Gate\",\"active\":true}");

		assertEquals(object, JsonElement.valueOf(JsonElement.stringify(object)));
		assertThrows(NullPointerException.class, () -> JsonElement.stringify(null));
		assertThrows(NullPointerException.class, () -> JsonElement.parse(null));
	}

	@Test
	public void shouldWrapNaturalJavaValues()
	{
		assertSame(JsonNull.INSTANCE, JsonElement.wrap(null));
		assertEquals(JsonString.wrap("value"), JsonElement.wrap("value"));
		assertSame(JsonBoolean.TRUE, JsonElement.wrap(true));
		assertEquals(JsonNumber.wrap(1), JsonElement.wrap(1));
		assertEquals(JsonArray.of(JsonNumber.wrap(1), JsonString.wrap("two")),
				JsonElement.wrap(List.of(1, "two")));
		assertEquals(JsonArray.of(JsonString.wrap("a"), JsonString.wrap("b")),
				JsonElement.wrap(new String[]{"a", "b"}));
		assertEquals(new JsonObject().setString("name", "Gate"),
				JsonElement.wrap(Map.of("name", "Gate")));
	}

	@Test
	public void shouldRejectArbitraryObjectAndNonStringMapKeyWhenWrapping()
	{
		assertThrows(ConversionException.class, () -> JsonElement.wrap(new WrappedType("value")));
		assertThrows(ConversionException.class, () -> JsonElement.wrap(Map.of(1, "value")));
	}

	@Test
	public void shouldEncodeDomainObjectsCollectionsAndMaps()
	{
		UserMock user = MockFactory.user();

		JsonObject object = (JsonObject) JsonElement.encode(user);
		JsonArray array = (JsonArray) JsonElement.encode(List.of(user));
		JsonArray values = (JsonArray) JsonElement.encode(new Object[]{user, null});
		JsonObject map = (JsonObject) JsonElement.encode(Map.of("user", user));

		assertEquals("User 1", object.getString("name").orElseThrow());
		assertEquals("User 1", ((JsonObject) array.get(0)).getString("name").orElseThrow());
		assertEquals("User 1", ((JsonObject) values.get(0)).getString("name").orElseThrow());
		assertSame(JsonNull.INSTANCE, values.get(1));
		assertEquals("User 1", ((JsonObject) map.get("user")).getString("name").orElseThrow());
	}

	@Test
	public void shouldRenderValuesForDisplay()
	{
		UserMock user = MockFactory.user();

		assertSame(JsonElement.UNDEFINED, JsonElement.render(null));
		assertSame(JsonBoolean.FALSE, JsonElement.render(false));
		assertEquals(JsonNumber.wrap(42), JsonElement.render(42));
		assertEquals(JsonString.wrap("User 1"), JsonElement.render(user.getName()));
		assertEquals(JsonArray.of(JsonString.wrap("User 1"), JsonNumber.wrap(10)),
				JsonArray.render(user.getName(), user.getLevel()));
	}

	@Test
	public void shouldResolveDefaultAndConcretePaths()
	{
		JsonObject object = new JsonObject().setString("name", "Gate").setInt("level", 10);
		JsonArray array = JsonArray.wrap("first", "second");
		JsonElement scalar = JsonString.wrap("value");

		assertEquals(JsonString.wrap("Gate"), object.path("name"));
		assertEquals(JsonString.wrap("Gate"), object.path(0));
		assertEquals(JsonNumber.wrap(10), object.path(-1));
		assertEquals(JsonString.wrap("second"), array.path(1));
		assertEquals(JsonString.wrap("second"), array.path(-1));
		assertSame(JsonNull.INSTANCE, array.path("1"));
		assertSame(JsonNull.INSTANCE, scalar.path("name"));
		assertSame(JsonNull.INSTANCE, scalar.path(0));
	}

	private record WrappedType(String value)
	{
	}
}