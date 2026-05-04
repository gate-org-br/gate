package gate.adapter.jsonRenderer;

import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RecordJsonRendererTest
{
	private final RecordJsonRenderer renderer = new RecordJsonRenderer();

	@Test
	public void testShouldRenderRecord()
	{
		var expected = new JsonObject();
		expected.put("name", JsonString.of("gate"));
		expected.put("amount", JsonString.of("21"));

		Assertions.assertEquals(expected, renderer.render(SampleRecord.class, new SampleRecord("gate", 21, null)));
	}

	private record SampleRecord(String name, Integer amount, String empty)
	{
	}
}
