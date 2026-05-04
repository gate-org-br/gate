package gate.adapter.jsonRenderer;

import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Month;

public class MonthJsonRendererTest
{
	private final MonthJsonRenderer renderer = new MonthJsonRenderer();

	@Test
	public void testShouldRenderMonth()
	{
		Assertions.assertEquals(JsonString.of("abril"), renderer.render(Month.class, Month.APRIL));
	}
}
