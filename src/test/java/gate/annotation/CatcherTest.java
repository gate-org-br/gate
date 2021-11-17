package gate.annotation;

import gate.base.Screen;
import gate.catcher.HTMLCommandCatcher;
import gate.catcher.CommandCatcher;
import gate.catcher.JSPCommandCatcher;
import gate.catcher.ObjectCatcher;
import gate.command.JSPCommand;
import org.junit.Assert;
import org.junit.Test;

public class CatcherTest
{

	@Catcher(JSPCommandCatcher.class)
	private static class Mock extends Screen
	{

		@Catcher(CommandCatcher.class)
		public void mock()
		{

		}

		@Catcher(HTMLCommandCatcher.class)
		private static class MockSubclass extends Mock
		{

			@Override
			@Catcher(CommandCatcher.class)
			public void mock()
			{

			}
		}

		@Catcher(HTMLCommandCatcher.class)
		private static class Mock2Subclass extends Mock
		{

			@Override
			public void mock()
			{

			}
		}
	}

	private static class Mock2 extends Screen
	{

		public JSPCommand mock()
		{
			return null;
		}

		public Object mock2()
		{
			return null;
		}

	}

	@Test
	public void test1() throws ReflectiveOperationException
	{
		var catcher = Catcher.Extractor.extract(Mock.class.getMethod("mock"));
		Assert.assertEquals(CommandCatcher.class, catcher);
	}

	@Test
	public void test2() throws ReflectiveOperationException
	{
		var catcher = Catcher.Extractor.extract(Mock.MockSubclass.class.getMethod("mock"));
		Assert.assertEquals(CommandCatcher.class, catcher);
	}

	@Test
	public void test3() throws ReflectiveOperationException
	{
		var catcher = Catcher.Extractor.extract(Mock.Mock2Subclass.class.getMethod("mock"));
		Assert.assertEquals(HTMLCommandCatcher.class, catcher);
	}

	@Test
	public void test4() throws ReflectiveOperationException
	{
		var catcher = Catcher.Extractor.extract(Mock2.class.getMethod("mock"));
		Assert.assertEquals(JSPCommandCatcher.class, catcher);
	}

	@Test
	public void test5() throws ReflectiveOperationException
	{
		var catcher = Catcher.Extractor.extract(Mock2.class.getMethod("mock2"));
		Assert.assertEquals(ObjectCatcher.class, catcher);
	}
}
