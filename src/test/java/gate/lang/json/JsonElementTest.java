package gate.lang.json;

import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonElementTest
{

	@Test
	public void shouldWrapNaturalJavaValues()
	{
		assertEquals(JsonNull.INSTANCE, JsonElement.wrap(null));
		assertEquals(JsonString.of("value"), JsonElement.wrap("value"));
		assertEquals(JsonBoolean.TRUE, JsonElement.wrap(true));
		assertEquals(JsonArray.of(JsonNumber.of(1), JsonString.of("two")),
				JsonElement.wrap(List.of(1, "two")));
		assertEquals(new JsonObject().setString("name", "Gate"),
				JsonElement.wrap(Map.of("name", "Gate")));
	}

	@Test
	public void shouldRejectArbitraryObjectWhenWrapping()
	{
		assertThrows(ConversionException.class, () -> JsonElement.wrap(new WrappedType("value")));
	}

	@Test
	public void shouldResolvePathByMatchingSegmentType()
	{
		JsonObject object = new JsonObject().setString("name", "Gate");
		JsonArray array = JsonArray.wrap("first", "second");

		assertEquals(JsonString.of("Gate"), object.path("name"));
		assertEquals(JsonNull.INSTANCE, object.path(0));
		assertEquals(JsonString.of("second"), array.path(1));
		assertEquals(JsonNull.INSTANCE, array.path("1"));
	}

	private record WrappedType(String value)
	{
	}
}
