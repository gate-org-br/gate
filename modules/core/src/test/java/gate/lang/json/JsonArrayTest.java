package gate.lang.json;

import gate.error.ConversionException;
import mock.MockFactory;
import mock.UserMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class JsonArrayTest
{

	@Test
	public void shouldParseStringifyAndPreserveElements()
	{
		JsonArray array = JsonArray.of(JsonBoolean.FALSE, JsonString.of("string"), JsonNumber.of(20));

		assertEquals(array, JsonArray.parse(JsonElement.stringify(array)));
		assertEquals(array, JsonArray.valueOf(array.toString()));
	}

	@Test
	public void shouldRejectInvalidOrNonArrayJson()
	{
		assertThrows(ConversionException.class, () -> JsonArray.parse("[1, 2"));
		assertThrows(ConversionException.class, () -> JsonArray.parse("{\"id\":1}"));
		assertThrows(NullPointerException.class, () -> JsonArray.parse(null));
		assertThrows(UnsupportedOperationException.class, () -> new JsonArray().decode(List.class));
	}

	@Test
	public void shouldWrapCollectionsArraysAndStreams()
	{
		assertEquals(JsonArray.of(JsonNumber.of(1), JsonString.of("two")), JsonArray.wrap(List.of(1, "two")));
		assertEquals(JsonArray.of(JsonString.of("a"), JsonString.of("b")), JsonArray.wrap("a", "b"));
		assertEquals(JsonArray.of(JsonBoolean.TRUE, JsonNull.INSTANCE), JsonArray.wrap(Stream.of(true, null)));
	}

	@Test
	public void shouldCreateArrayOfJsonElementsAndReplaceNulls()
	{
		JsonArray array = JsonArray.of(JsonBoolean.TRUE, null, JsonString.of("value"));

		assertEquals(JsonBoolean.TRUE, array.get(0));
		assertSame(JsonNull.INSTANCE, array.get(1));
		assertEquals(JsonString.of("value"), array.get(2));
	}

	@Test
	public void shouldRenderValuesAsTextElements()
	{
		JsonArray array = JsonArray.render(List.of(true, false, 1));

		assertEquals(JsonBoolean.TRUE, array.get(0));
		assertEquals(JsonBoolean.FALSE, array.get(1));
		assertEquals(JsonNumber.of(1), array.get(2));
		assertEquals(JsonArray.render("a", "b"), JsonArray.of(JsonString.of("a"), JsonString.of("b")));
	}

	@Test
	public void shouldCreateEntriesAndRenderedEntries()
	{
		List<UserMock> users = List.of(MockFactory.user(1), MockFactory.user(2), MockFactory.user(3));

		JsonArray entries = JsonArray.entries(users, UserMock::getName, UserMock::getId);
		JsonArray withProperties = JsonArray.entries(users, UserMock::getName, UserMock::getId,
				user -> new JsonObject().setString("role", user.getRole().getName()));
		JsonArray rendered = JsonArray.render(users, UserMock::getName, UserMock::getLevel);

		assertEquals("User 1", ((JsonObject) entries.get(0)).getString("label").orElseThrow());
		assertEquals(2, ((JsonObject) entries.get(1)).getInt("value").orElseThrow());
		assertEquals("Role", ((JsonObject) withProperties.get(2))
				.getJsonObject("properties").orElseThrow()
				.getString("role").orElseThrow());
		assertEquals(10, ((JsonObject) rendered.get(0)).getInt("value").orElseThrow());
	}

	@Test
	public void shouldInsertFluentlyAndResolvePath()
	{
		JsonArray array = new JsonArray()
				.insert(JsonString.of("second"))
				.insert(0, JsonString.of("first"));

		assertEquals(JsonString.of("first"), array.get(0));
		assertEquals(JsonString.of("second"), array.path(1));
		assertEquals(JsonString.of("second"), array.path(-1));
		assertEquals(JsonString.of("first"), array.path(-2));
		assertSame(JsonNull.INSTANCE, array.path(2));
		assertSame(JsonNull.INSTANCE, array.path(-3));
		assertSame(JsonNull.INSTANCE, array.path("0"));
	}

	@Test
	public void shouldReturnOptionalValuesByIndexSafely()
	{
		JsonArray array = JsonArray.of(JsonString.of("value"), JsonNumber.of(1), JsonNull.INSTANCE);

		assertEquals("value", array.getString(0).orElseThrow());
		assertEquals(JsonNumber.of(1), array.getJsonElement(1).orElseThrow());
		assertTrue(array.getString(1).isEmpty());
		assertTrue(array.getString(-1).isEmpty());
		assertTrue(array.getJsonElement(-1).isEmpty());
		assertTrue(array.getJsonElement(99).isEmpty());
	}

	@Test
	public void shouldDecodeToListAndSet()
	{
		JsonArray array = JsonArray.of(JsonNumber.of(1), JsonNumber.of(2), JsonNumber.of(2));

		List<Integer> list = array.decode(List.class, Integer.class);
		Set<Integer> set = array.decode(Set.class, Integer.class);

		assertEquals(List.of(1, 2, 2), list);
		assertEquals(new LinkedHashSet<>(List.of(1, 2)), set);
	}

	@Test
	public void shouldUnwrapToNaturalJavaList()
	{
		JsonArray array = JsonArray.of(JsonString.of("value"), JsonNumber.of(1), JsonBoolean.TRUE,
				new JsonObject().setString("name", "User 1"), JsonNull.INSTANCE);

		assertEquals(Arrays.asList("value", BigDecimal.ONE, true, java.util.Map.of("name", "User 1"), null),
				array.unwrap());
	}

	@Test
	public void shouldExposeListOperations()
	{
		JsonArray array = new JsonArray(JsonArray.of(JsonString.of("a"), JsonString.of("b")));

		assertEquals(2, array.size());
		assertTrue(array.contains(JsonString.of("a")));
		assertEquals(0, array.indexOf(JsonString.of("a")));
		assertEquals(1, array.lastIndexOf(JsonString.of("b")));
		assertArrayEquals(new Object[]{JsonString.of("a"), JsonString.of("b")}, array.toArray());

		array.set(1, JsonString.of("c"));
		array.add(1, JsonString.of("b"));
		assertEquals(JsonArray.of(JsonString.of("a"), JsonString.of("b"), JsonString.of("c")), array);
		assertEquals(List.of(JsonString.of("b"), JsonString.of("c")), array.subList(1, 3));

		assertTrue(array.remove(JsonString.of("b")));
		assertEquals(JsonString.of("c"), array.remove(1));
		assertTrue(array.addAll(JsonArray.of(JsonString.of("x"), JsonString.of("y"))));
		assertTrue(array.containsAll(JsonArray.of(JsonString.of("x"))));
		assertTrue(array.retainAll(JsonArray.of(JsonString.of("x"))));
		assertEquals(JsonArray.of(JsonString.of("x")), array);
		assertTrue(array.addAll(1, JsonArray.of(JsonString.of("z"))));
		assertTrue(array.removeAll(JsonArray.of(JsonString.of("z"))));

		assertNotNull(array.iterator());
		assertNotNull(array.listIterator());
		assertNotNull(array.listIterator(0));
	}
}