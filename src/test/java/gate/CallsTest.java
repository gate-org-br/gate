package gate;

import gate.annotation.AllowMethod;
import gate.base.Screen;
import gate.http.HttpMethod;
import gate.type.RequestCommand;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CallsTest
{

	@Test
	@SuppressWarnings("unchecked")
	public void testRegisterSupportsCanonicalInnerScreenName()
	{
		Calls calls = new Calls();
		calls.register(List.of((Class<Screen>) (Class<?>) TestRegistryParentScreen.ChildScreen.class));

		assertTrue(calls.get(new RequestCommand("gate", "TestRegistryParent.Child", null)).isPresent());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testRegisterReadsAllowedHttpMethods()
	{
		Calls calls = new Calls();
		calls.register(List.of((Class<Screen>) (Class<?>) MethodScreen.class));

		Call call = calls.get(new RequestCommand("gate", "Method", null)).orElseThrow();
		assertTrue(call.allowsHttpMethod("GET"));
		assertTrue(call.allowsHttpMethod("POST"));
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
	@AllowMethod(HttpMethod.GET)
	@AllowMethod(HttpMethod.POST)
	public void call()
	{
	}
}