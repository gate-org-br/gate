package gate.adapter.jsonRenderer;

import gate.annotation.Name;
import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumJsonRendererTest
{
	private final EnumJsonRenderer renderer = new EnumJsonRenderer();

	@Test
	public void testShouldRenderEnum()
	{
		Assertions.assertEquals(JsonString.wrap("Active"), renderer.renderJson(Status.class, Status.ACTIVE));
	}

	private enum Status
	{
		@Name("Active")
		ACTIVE
	}
}