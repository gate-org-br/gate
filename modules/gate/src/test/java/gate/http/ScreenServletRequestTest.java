package gate.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class ScreenServletRequestTest
{
	@Test
	void shouldPopulateLeafCollectionWithConvertedValues()
	{
		var request = new ScreenServletRequest(TestServletSupport.requestValues(
				Map.of("permissions[]", new String[]{"READ", "WRITE"}),
				Map.of(), null, "POST", "/Gate").request());

		var result = new CollectionFormMock();
		request.getPropertyGraph(CollectionFormMock.class)
				.populate(result, request::getParameterValue);

		Assertions.assertEquals(List.of(Permission.READ, Permission.WRITE), result.getPermissions());
	}

	@Test
	void shouldPopulateLeafSetWithConvertedValues()
	{
		var request = new ScreenServletRequest(TestServletSupport.requestValues(
				Map.of("permissions[]", new String[]{"READ", "WRITE", "READ"}),
				Map.of(), null, "POST", "/Gate").request());

		var result = new SetFormMock();
		request.getPropertyGraph(SetFormMock.class)
				.populate(result, request::getParameterValue);

		Assertions.assertEquals(Set.of(Permission.READ, Permission.WRITE), result.getPermissions());
	}

	@Test
	void shouldPopulateLeafSetWithPartValues()
	{
		AtomicInteger deletes = new AtomicInteger();
		var request = new ScreenServletRequest(requestParts(
				part("names[]", "Ana", deletes),
				part("names[]", "Bia", deletes),
				part("names[]", "Ana", deletes)));

		var result = new PartSetFormMock();
		request.getPropertyGraph(PartSetFormMock.class)
				.populate(result, request::getParameterValue);

		Assertions.assertEquals(Set.of("Ana", "Bia"), result.getNames());
		Assertions.assertEquals(3, deletes.get());
	}

	@Test
	void shouldPopulateLeafArrayWithConvertedValues()
	{
		var request = new ScreenServletRequest(TestServletSupport.requestValues(
				Map.of("permissions[]", new String[]{"READ", "WRITE"}),
				Map.of(), null, "POST", "/Gate").request());

		var result = new ArrayFormMock();
		request.getPropertyGraph(ArrayFormMock.class)
				.populate(result, request::getParameterValue);

		Assertions.assertArrayEquals(new Permission[]{Permission.READ, Permission.WRITE}, result.getPermissions());
	}

	@Test
	void shouldPopulateIndexedArrayWithConvertedValues()
	{
		var request = new ScreenServletRequest(TestServletSupport.request(Map.of(
						"permissions[0]", "READ",
						"permissions[1]", "WRITE"),
				Map.of(), null, "POST", "/Gate").request());

		var result = new ArrayFormMock();
		request.getPropertyGraph(ArrayFormMock.class)
				.populate(result, request::getParameterValue);

		Assertions.assertArrayEquals(new Permission[]{Permission.READ, Permission.WRITE}, result.getPermissions());
	}

	public static class CollectionFormMock
	{
		private List<Permission> permissions;

		public List<Permission> getPermissions()
		{
			return permissions;
		}

		public void setPermissions(List<Permission> permissions)
		{
			this.permissions = permissions;
		}
	}

	public static class ArrayFormMock
	{
		private Permission[] permissions;

		public Permission[] getPermissions()
		{
			return permissions;
		}

		public void setPermissions(Permission[] permissions)
		{
			this.permissions = permissions;
		}
	}

	public static class SetFormMock
	{
		private Set<Permission> permissions;

		public Set<Permission> getPermissions()
		{
			return permissions;
		}

		public void setPermissions(Set<Permission> permissions)
		{
			this.permissions = permissions;
		}
	}

	public static class PartSetFormMock
	{
		private Set<String> names;

		public Set<String> getNames()
		{
			return names;
		}

		public void setNames(Set<String> names)
		{
			this.names = names;
		}
	}

	enum Permission
	{
		READ, WRITE
	}

	private static HttpServletRequest requestParts(Part... parts)
	{
		return (HttpServletRequest) Proxy.newProxyInstance(
				ScreenServletRequestTest.class.getClassLoader(),
				new Class[]{HttpServletRequest.class},
				(proxy, called, args) ->
				{
					String name = called.getName();
					return switch (name)
					{
						case "getContentType" -> "multipart/form-data";
						case "getParts" -> List.of(parts);
						case "getParameter", "getParameterValues", "getHeader", "getCookies" -> null;
						case "getParameterNames" -> Collections.emptyEnumeration();
						case "getMethod" -> "POST";
						case "getRequestURI" -> "/Gate";
						case "getReader" -> new BufferedReader(new StringReader(""));
						default -> defaultValue(called.getReturnType());
					};
				});
	}

	private static Part part(String name, String content, AtomicInteger deletes)
	{
		return (Part) Proxy.newProxyInstance(
				ScreenServletRequestTest.class.getClassLoader(),
				new Class[]{Part.class},
				(proxy, called, args) ->
				{
					return switch (called.getName())
					{
						case "getName" -> name;
						case "getSize" -> (long) content.length();
						case "getInputStream" -> new ByteArrayInputStream(
								content.getBytes(StandardCharsets.UTF_8));
						case "delete" ->
						{
							deletes.incrementAndGet();
							yield null;
						}
						default -> defaultValue(called.getReturnType());
					};
				});
	}

	private static Object defaultValue(Class<?> type)
	{
		if (!type.isPrimitive())
			return null;
		if (type == boolean.class)
			return false;
		if (type == byte.class)
			return (byte) 0;
		if (type == short.class)
			return (short) 0;
		if (type == int.class)
			return 0;
		if (type == long.class)
			return 0L;
		if (type == float.class)
			return 0F;
		if (type == double.class)
			return 0D;
		if (type == char.class)
			return '\0';
		return null;
	}
}