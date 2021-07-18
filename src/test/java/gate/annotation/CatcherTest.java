package gate.annotation;

import gate.base.Screen;
import gate.catcher.HTMLCatcher;
import gate.catcher.HideCatcher;
import gate.catcher.JSPCatcher;
import gate.catcher.ObjectCatcher;
import gate.type.JSP;
import org.junit.Assert;
import org.junit.Test;

public class CatcherTest
{

	@Catcher(JSPCatcher.class)
	private static class Mock extends Screen
	{

		@Catcher(HideCatcher.class)
		public void mock()
		{

		}

		@Catcher(HTMLCatcher.class)
		private static class MockSubclass extends Mock
		{

			@Override
			@Catcher(HideCatcher.class)
			public void mock()
			{

			}
		}

		@Catcher(HTMLCatcher.class)
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

		public JSP mock()
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
		Assert.assertEquals(HideCatcher.class, catcher);
	}

	@Test
	public void test2() throws ReflectiveOperationException
	{
		var catcher = Catcher.Extractor.extract(Mock.MockSubclass.class.getMethod("mock"));
		Assert.assertEquals(HideCatcher.class, catcher);
	}

	@Test
	public void test3() throws ReflectiveOperationException
	{
		var catcher = Catcher.Extractor.extract(Mock.Mock2Subclass.class.getMethod("mock"));
		Assert.assertEquals(HTMLCatcher.class, catcher);
	}

	@Test
	public void test4() throws ReflectiveOperationException
	{
		var catcher = Catcher.Extractor.extract(Mock2.class.getMethod("mock"));
		Assert.assertEquals(JSPCatcher.class, catcher);
	}

	@Test
	public void test5() throws ReflectiveOperationException
	{
		var catcher = Catcher.Extractor.extract(Mock2.class.getMethod("mock2"));
		Assert.assertEquals(ObjectCatcher.class, catcher);
	}
}
