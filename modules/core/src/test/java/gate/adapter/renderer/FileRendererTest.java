package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class FileRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = new File("test.txt");
		Assertions.assertEquals("test.txt", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = new File("test.txt");
		Assertions.assertEquals("File: test.txt", Renderer.render(value, "File: %s"));
	}
}
