package gate.http;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TestServletSupport
{
	private TestServletSupport()
	{
	}

	public static RequestState request()
	{
		return request(Map.of(), Map.of(), null, "GET", "/Gate");
	}

	public static RequestState request(Map<String, String> parameters,
	                                   Map<String, String> headers,
	                                   Cookie[] cookies,
	                                   String method,
	                                   String requestUri)
	{
		Map<String, Object> attributes = new HashMap<>();
		HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
				TestServletSupport.class.getClassLoader(),
				new Class[]{HttpServletRequest.class},
				(proxy, called, args) ->
				{
					String name = called.getName();

					return switch (name)
					{
						case "getParameter" -> parameters.get((String) args[0]);
						case "getParameterValues" ->
						{
							String value = parameters.get((String) args[0]);
							yield value != null ? new String[]{value} : null;
						}
						case "getParameterNames" -> enumeration(parameters.keySet());
						case "getHeader" -> headers.get((String) args[0]);
						case "getCookies" -> cookies;
						case "getAttribute" -> attributes.get((String) args[0]);
						case "setAttribute" ->
						{
							attributes.put((String) args[0], args[1]);
							yield null;
						}
						case "removeAttribute" ->
						{
							attributes.remove((String) args[0]);
							yield null;
						}
						case "getMethod" -> method;
						case "getRequestURI" -> requestUri;
						case "getRequestURL" -> new StringBuffer("http://localhost" + requestUri);
						case "getPathInfo", "getContentType", "getQueryString", "getContextPath", "getServletPath" -> null;
						case "getParts" -> List.of();
						case "getReader" -> new BufferedReader(new java.io.StringReader(""));
						case "getCharacterEncoding" -> "UTF-8";
						case "setCharacterEncoding" -> null;
						default -> defaultValue(called.getReturnType());
					};
				});

		return new RequestState(request, attributes);
	}

	public static ResponseState response()
	{
		Map<String, List<String>> headers = new HashMap<>();
		StringWriter body = new StringWriter();
		HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
				TestServletSupport.class.getClassLoader(),
				new Class[]{HttpServletResponse.class},
				(proxy, called, args) ->
				{
					String name = called.getName();

					return switch (name)
					{
						case "addCookie" ->
						{
							Cookie cookie = (Cookie) args[0];
							StringBuilder value = new StringBuilder()
									.append(cookie.getName())
									.append("=")
									.append(cookie.getValue());
							if (cookie.getPath() != null)
								value.append("; Path=").append(cookie.getPath());
							value.append("; Max-Age=").append(cookie.getMaxAge());
							headers.computeIfAbsent("Set-Cookie", key -> new ArrayList<>())
									.add(value.toString());
							yield null;
						}
						case "addHeader" ->
						{
							headers.computeIfAbsent((String) args[0], key -> new ArrayList<>())
									.add((String) args[1]);
							yield null;
						}
						case "setHeader" ->
						{
							headers.put((String) args[0], new ArrayList<>(List.of((String) args[1])));
							yield null;
						}
						case "getWriter" -> new PrintWriter(body, true);
						default -> defaultValue(called.getReturnType());
					};
				});

		return new ResponseState(response, headers, body);
	}

	public static ChainState chain()
	{
		boolean[] called = {false};
		FilterChain chain = (FilterChain) Proxy.newProxyInstance(
				TestServletSupport.class.getClassLoader(),
				new Class[]{FilterChain.class},
				(proxy, method, args) ->
				{
					if ("doFilter".equals(method.getName()))
						called[0] = true;

					return null;
				});

		return new ChainState(chain, called);
	}

	private static Enumeration<String> enumeration(Iterable<String> values)
	{
		List<String> list = new ArrayList<>();
		values.forEach(list::add);
		return Collections.enumeration(list);
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

	public record RequestState(HttpServletRequest request, Map<String, Object> attributes)
	{
	}

	public record ResponseState(HttpServletResponse response,
	                            Map<String, List<String>> headers,
	                            StringWriter body)
	{
		public String header(String name)
		{
			return headers.getOrDefault(name, List.of()).stream().findFirst().orElse(null);
		}
	}

	public record ChainState(FilterChain chain, boolean[] called)
	{
		public boolean wasCalled()
		{
			return called[0];
		}
	}
}
