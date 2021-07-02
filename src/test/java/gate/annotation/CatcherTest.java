package gate.annotation;

import gate.catcher.HTMLCatcher;
import gate.catcher.HideCatcher;
import gate.catcher.JSPCatcher;
import gate.error.AppException;
import gate.error.NotFoundException;
import org.junit.Assert;
import org.junit.Test;

public class CatcherTest
{

	@Catcher(type = AppException.class,
		value = JSPCatcher.class)
	private static class Mock
	{

		@Catcher(type = NotFoundException.class,
			value = HideCatcher.class)
		public void mock()
		{

		}

		@Catcher(type = AppException.class,
			value = HTMLCatcher.class)
		private static class MockSubclass extends Mock
		{

			@Catcher(type = NotFoundException.class,
				value = HideCatcher.class)
			public void mock()
			{

			}
		}
	}

	@Test
	public void test1() throws ReflectiveOperationException
	{
		var catchers = Catcher.Extractor.extract(Mock.class.getMethod("mock"));
		Assert.assertEquals(JSPCatcher.class, catchers.get(AppException.class));
		Assert.assertEquals(HideCatcher.class, catchers.get(NotFoundException.class));
	}

	@Test
	public void test2() throws ReflectiveOperationException
	{
		var catchers = Catcher.Extractor.extract(Mock.MockSubclass.class.getMethod("mock"));
		Assert.assertEquals(HideCatcher.class, catchers.get(NotFoundException.class));
		Assert.assertEquals(HTMLCatcher.class, catchers.get(AppException.class));
	}
}
