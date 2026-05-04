package gate.adapter.jsonRenderer;

import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectJsonRendererTest
{
	private final ObjectJsonRenderer renderer = new ObjectJsonRenderer();

	@Test
	public void testShouldRenderObject()
	{
		var expected = new JsonObject();
		expected.put("name", JsonString.of("parent"));
		expected.put("amount", JsonString.of("2"));
		expected.put("child", child("child"));

		Assertions.assertEquals(expected, renderer.render(Bean.class, new Bean("parent", 2, new Bean("child", 1, null))));
	}

	@Test
	public void testShouldSkipEmptyObjectAttributes()
	{
		var expected = new JsonObject();
		expected.put("name", JsonString.of("parent"));
		expected.put("amount", JsonString.of("2"));

		Assertions.assertEquals(expected, renderer.render(Bean.class, new Bean("parent", 2, null)));
	}

	private JsonObject child(String name)
	{
		var child = new JsonObject();
		child.put("name", JsonString.of(name));
		child.put("amount", JsonString.of("1"));
		return child;
	}

	public static class Bean
	{
		private final String name;
		private final Integer amount;
		private final Bean child;

		public Bean(String name, Integer amount, Bean child)
		{
			this.name = name;
			this.amount = amount;
			this.child = child;
		}
	}
}
