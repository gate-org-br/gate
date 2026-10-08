package gate;

import gate.annotation.Get;
import gate.annotation.Post;
import gate.base.Screen;
import gate.type.RequestCommand;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CallsTest
{

	@Test
	@SuppressWarnings("unchecked")
	public void testRegisterSupportsCanonicalInnerScreenName()
	{
		CallRegistry calls = new CallRegistry();
		calls.register(List.of((Class<Screen>) (Class<?>) TestRegistryParentScreen.ChildScreen.class));

		assertTrue(calls.contains(new RequestCommand("gate", "TestRegistryParent.Child", null)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testRegisterReadsAllowedHttpMethods()
	{
		CallRegistry calls = new CallRegistry();
		calls.register(List.of((Class<Screen>) (Class<?>) MethodScreen.class));

		var command = new RequestCommand("gate", "Method", null);
		assertTrue(calls.get("GET", command).isPresent());
		assertTrue(calls.get("POST", command).isPresent());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMetadataLookupFallbacksToAnyMethod()
	{
		CallRegistry calls = new CallRegistry();
		calls.register(List.of((Class<Screen>) (Class<?>) TestRegistryParentScreen.ChildScreen.class));

		var command = new RequestCommand("gate", "TestRegistryParent.Child", null);
		assertTrue(calls.get("GET", command).isPresent());
		assertTrue(calls.getMetadata(command).isPresent());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testRegisterAllowsSameActionWithDifferentHttpMethods()
	{
		CallRegistry calls = new CallRegistry();
		calls.register(List.of((Class<Screen>) (Class<?>) OverloadedHttpMethodScreen.class));

		var command = new RequestCommand("gate", "OverloadedHttpMethod", "Save");
		assertTrue(calls.get("GET", command).isPresent());
		assertTrue(calls.get("POST", command).isPresent());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testRegisterRejectsSameActionWithOverlappingHttpMethods()
	{
		CallRegistry calls = new CallRegistry();

		assertThrows(IllegalStateException.class,
				() -> calls.register(List.of((Class<Screen>) (Class<?>) DuplicatedHttpMethodScreen.class)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testRegisterRejectsSameActionWhenAnyMethodOverlaps()
	{
		CallRegistry calls = new CallRegistry();

		assertThrows(IllegalStateException.class,
				() -> calls.register(List.of((Class<Screen>) (Class<?>) DuplicatedDefaultMethodScreen.class)));
	}
}

class TestRegistryParentScreen extends Screen
{
	public static class ChildScreen extends Screen
	{
		public void call()
		{
		}
	}
}

class MethodScreen extends Screen
{
	@Get
	@Post
	public void call()
	{
	}
}

class OverloadedHttpMethodScreen extends Screen
{
	@Get
	public void callSave()
	{
	}

	@Post
	public void callSave(String body)
	{
	}
}

class DuplicatedHttpMethodScreen extends Screen
{
	@Get
	public void callSave()
	{
	}

	@Get
	public void callSave(String body)
	{
	}
}

class DuplicatedDefaultMethodScreen extends Screen
{
	public void callSave()
	{
	}

	@Post
	public void callSave(String body)
	{
	}
}