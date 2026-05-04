package gate.adapter.jsonRenderer;

import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

public class DayOfWeekJsonRendererTest
{
	private final DayOfWeekJsonRenderer renderer = new DayOfWeekJsonRenderer();

	@Test
	public void testShouldRenderDayOfWeek()
	{
		Assertions.assertEquals(JsonString.of("quarta-feira"), renderer.render(DayOfWeek.class, DayOfWeek.WEDNESDAY));
	}
}
