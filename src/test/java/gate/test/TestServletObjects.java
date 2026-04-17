package gate.test;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Instance;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.*;

@SuppressWarnings("unchecked")
public final class TestServletObjects
{
	private TestServletObjects()
	{
	}

	public static class RequestData
	{
		public final Map<String, Object> attributes = new HashMap<>();
		public final Map<String, String> parameters = new HashMap<>();
		public final Map<String, String> headers = new HashMap<>();
		public Cookie[] cookies;
		public String pathInfo;
		public String requestURI = "/";
		public String contextPath = "/";
		public String contentType;
		public Locale locale = Locale.getDefault();
		public Collection<Part> parts = Collections.emptyList();
	}

	public static class ResponseData
	{
		public final List<Cookie> cookies = new ArrayList<>();
		public final Map<String, List<String>> headers = new HashMap<>();
		public final StringWriter body = new StringWriter();
		public String redirect;
		public String contentType;
		public String characterEncoding;
		public Locale locale = Locale.getDefault();
		public int status = HttpServletResponse.SC_OK;
		public boolean committed;
	}

	public static HttpServletRequest request(RequestData data)
	{
		ServletContext context = (ServletContext) Proxy.newProxyInstance(
				ServletContext.class.getClassLoader(),
				new Class[]{ServletContext.class},
				(proxy, method, args) ->
				{
					if ("getContextPath".equals(method.getName()))
						return data.contextPath;
					return defaultValue(method.getReturnType());
				});

		return (HttpServletRequest) Proxy.newProxyInstance(
				HttpServletRequest.class.getClassLoader(),
				new Class[]{HttpServletRequest.class},
				(proxy, method, args) ->
				{
					return switch (method.getName())
					{
						case "getAttribute" -> data.attributes.get((String) args[0]);
						case "setAttribute" ->
						{
							data.attributes.put((String) args[0], args[1]);
							yield null;
						}
						case "removeAttribute" ->
						{
							data.attributes.remove((String) args[0]);
							yield null;
						}
						case "getParameter" -> data.parameters.get((String) args[0]);
						case "getParameterValues" ->
						{
							String value = data.parameters.get((String) args[0]);
							yield value == null ? null : new String[]{value};
						}
						case "getParameterNames" -> Collections.enumeration(data.parameters.keySet());
						case "getHeader" -> data.headers.get((String) args[0]);
						case "getCookies" -> data.cookies;
						case "getPathInfo" -> data.pathInfo;
						case "getRequestURI" -> data.requestURI;
						case "getServletContext" -> context;
						case "getContentType" -> data.contentType;
						case "getParts" -> data.parts;
						case "getLocale" -> data.locale;
						case "setCharacterEncoding" -> null;
						default -> defaultValue(method.getReturnType());
					};
				});
	}

	public static HttpServletResponse response(ResponseData data)
	{
		return (HttpServletResponse) Proxy.newProxyInstance(
				HttpServletResponse.class.getClassLoader(),
				new Class[]{HttpServletResponse.class},
				(proxy, method, args) ->
				{
					return switch (method.getName())
					{
						case "addCookie" ->
						{
							data.cookies.add((Cookie) args[0]);
							yield null;
						}
						case "addHeader" ->
						{
							data.headers.computeIfAbsent((String) args[0], key -> new ArrayList<>())
									.add((String) args[1]);
							yield null;
						}
						case "setHeader" ->
						{
							data.headers.put((String) args[0], new ArrayList<>(List.of((String) args[1])));
							yield null;
						}
						case "sendRedirect" ->
						{
							data.redirect = (String) args[0];
							data.committed = true;
							yield null;
						}
						case "getWriter" -> new PrintWriter(data.body);
						case "isCommitted" -> data.committed;
						case "setContentType" ->
						{
							data.contentType = (String) args[0];
							yield null;
						}
						case "setCharacterEncoding" ->
						{
							data.characterEncoding = (String) args[0];
							yield null;
						}
						case "setLocale" ->
						{
							data.locale = (Locale) args[0];
							yield null;
						}
						case "setStatus" ->
						{
							data.status = (Integer) args[0];
							yield null;
						}
						case "getStatus" -> data.status;
						default -> defaultValue(method.getReturnType());
					};
				});
	}

	public static <T> Instance<T> instance(T value)
	{
		return (Instance<T>) Proxy.newProxyInstance(
				Instance.class.getClassLoader(),
				new Class[]{Instance.class},
				(proxy, method, args) ->
				{
					if ("get".equals(method.getName()))
						return value;
					if ("iterator".equals(method.getName()))
						return Collections.singleton(value).iterator();
					return defaultValue(method.getReturnType());
				});
	}

	public static <T> Event<T> event(List<T> fired)
	{
		return (Event<T>) Proxy.newProxyInstance(
				Event.class.getClassLoader(),
				new Class[]{Event.class},
				(proxy, method, args) ->
				{
					if ("fire".equals(method.getName()) || "fireAsync".equals(method.getName()))
					{
						fired.add((T) args[0]);
						return null;
					}
					return proxy;
				});
	}

	public static FilterChain chain(Runnable runnable)
	{
		return new FilterChain()
		{
			@Override
			public void doFilter(ServletRequest request, ServletResponse response)
			{
				runnable.run();
			}
		};
	}

	private static Object defaultValue(Class<?> type)
	{
		if (!type.isPrimitive())
			return null;
		return switch (type.getName())
		{
			case "boolean" -> false;
			case "byte" -> (byte) 0;
			case "short" -> (short) 0;
			case "int" -> 0;
			case "long" -> 0L;
			case "float" -> 0f;
			case "double" -> 0d;
			case "char" -> '\0';
			default -> null;
		};
	}
}