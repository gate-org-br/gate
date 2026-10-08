package gate;

import gate.base.Screen;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NameResolverTest
{

	@Test
	public void testScreenUsesDotNotationRecursively()
	{
		assertEquals("TestParent.Child.GrandChild", NameResolver.screen(TestParentScreen.ChildScreen.GrandChildScreen.class));
	}

	@Test
	public void testScreenUsesDotNotationForImmediateInnerScreen()
	{
		assertEquals("TestParent.Child", NameResolver.screen(TestParentScreen.ChildScreen.class));
	}
}

class TestParentScreen extends Screen
{
	public static class ChildScreen extends Screen
	{
		public static class GrandChildScreen extends Screen
		{
		}
	}
}
