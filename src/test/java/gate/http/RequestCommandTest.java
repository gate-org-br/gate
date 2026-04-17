package gate.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestCommandTest
{

	@Test
	public void shouldKeepExplicitParameters()
	{
		RequestCommand command = RequestCommand.from("module", "screen", "action", "/ignored/path");

		assertEquals("module", command.module());
		assertEquals("screen", command.screen());
		assertEquals("action", command.action());
	}

	@Test
	public void shouldParseSingleSegmentPath()
	{
		RequestCommand command = RequestCommand.from(null, null, null, "/module");

		assertEquals("module", command.module());
		assertNull(command.screen());
		assertNull(command.action());
	}

	@Test
	public void shouldParseTwoSegmentPath()
	{
		RequestCommand command = RequestCommand.from(null, null, null, "/module/screen");

		assertEquals("module", command.module());
		assertEquals("screen", command.screen());
		assertNull(command.action());
	}

	@Test
	public void shouldParseThreeSegmentPath()
	{
		RequestCommand command = RequestCommand.from(null, null, null, "/module/screen/action");

		assertEquals("module", command.module());
		assertEquals("screen", command.screen());
		assertEquals("action", command.action());
	}

	@Test
	public void shouldIgnoreSegmentsAfterAction()
	{
		RequestCommand command = RequestCommand.from(null, null, null, "/module/screen/action/xxx/yyy/zzz");

		assertEquals("module", command.module());
		assertEquals("screen", command.screen());
		assertEquals("action", command.action());
	}

	@Test
	public void shouldParseTrailingSlashAfterSingleSegment()
	{
		RequestCommand command = RequestCommand.from(null, null, null, "/module/");

		assertEquals("module", command.module());
		assertEquals("", command.screen());
		assertNull(command.action());
	}

	@Test
	public void shouldParseTrailingSlashAfterTwoSegments()
	{
		RequestCommand command = RequestCommand.from(null, null, null, "/module/screen/");

		assertEquals("module", command.module());
		assertEquals("screen", command.screen());
		assertEquals("", command.action());
	}

	@Test
	public void shouldNormalizeBlankValuesToEmptyCommand()
	{
		RequestCommand command = RequestCommand.from(" ", "", null, null);

		assertTrue(command.isEmpty());
	}
}
