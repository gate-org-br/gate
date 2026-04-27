package gate;

import gate.entity.User;
import gate.http.TestServletSupport;
import gate.security.Credentials;
import gate.type.ID;
import jakarta.enterprise.inject.Instance;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SlidingSessionFilterTest
{
	@Test
	void testRefreshesCookieSession()
			throws Exception
	{
		var token = Credentials.create(ID.valueOf(1), null, null).toString();
		var request = TestServletSupport.request(
				java.util.Map.of(),
				java.util.Map.of(),
				new Cookie[]{new Cookie("subject", token)},
				"GET",
				"/Gate");
		var response = TestServletSupport.response();
		var chain = TestServletSupport.chain();
		request.attributes().put(User.class.getName(), new User().setId(ID.valueOf(1)));

		filter(new User().setId(ID.valueOf(1))).doFilter(request.request(), response.response(), chain.chain());

		assertTrue(chain.wasCalled());
		assertNotNull(response.header("Set-Cookie"));
		assertTrue(response.header("Set-Cookie").contains("subject="));
		assertTrue(response.header("Set-Cookie").contains("Max-Age=3600"));
	}

	@Test
	void testRefreshesBearerSession()
			throws Exception
	{
		var token = Credentials.create(ID.valueOf(1), null, null).toString();
		var request = TestServletSupport.request(
				java.util.Map.of(),
				java.util.Map.of("Authorization", "Bearer " + token),
				null,
				"GET",
				"/Gate");
		var response = TestServletSupport.response();
		var chain = TestServletSupport.chain();
		request.attributes().put(User.class.getName(), new User().setId(ID.valueOf(1)));

		filter(new User().setId(ID.valueOf(1))).doFilter(request.request(), response.response(), chain.chain());

		assertTrue(chain.wasCalled());
		assertNotNull(response.header("X-Access-Token"));
	}

	@Test
	void testIgnoresStaticRequests()
	{
		var token = Credentials.create(ID.valueOf(1), null, null).toString();
		var request = TestServletSupport.request(
				java.util.Map.of(),
				java.util.Map.of(),
				new Cookie[]{new Cookie("subject", token)},
				"GET",
				"/gate/app.js");
		var response = TestServletSupport.response();
		var chain = TestServletSupport.chain();

		assertDoesNotThrow(() -> filter(new User().setId(ID.valueOf(1)))
				.doFilter(request.request(), response.response(), chain.chain()));

		assertTrue(chain.wasCalled());
		assertTrue(response.headers().isEmpty());
	}

	private static SlidingSessionFilter filter(User user)
	{
		var filter = new SlidingSessionFilter();
		filter.userInstance = instance(user);
		return filter;
	}

	@SuppressWarnings("unchecked")
	private static Instance<User> instance(User user)
	{
		return (Instance<User>) Proxy.newProxyInstance(
				SlidingSessionFilterTest.class.getClassLoader(),
				new Class[]{Instance.class},
				(proxy, method, args) -> switch (method.getName())
				{
					case "get" -> user;
					case "iterator" -> Stream.of(user).iterator();
					case "isResolvable" -> true;
					case "isUnsatisfied", "isAmbiguous" -> false;
					case "destroy" -> null;
					case "select" -> proxy;
					case "handles" -> Stream.empty();
					default -> throw new UnsupportedOperationException(method.getName());
				});
	}
}