package gate.producer;

import gate.CDITestSupport;
import gate.SlidingSessionFilter;
import gate.annotation.Current;
import gate.entity.Role;
import gate.entity.User;
import gate.http.TestServletSupport;
import gate.security.Credentials;
import gate.type.ID;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@EnableWeld
class UserProducerTest
{
	@WeldSetup
	WeldInitiator weld = WeldInitiator.from(UserProducer.class,
					CDITestSupport.HttpServletRequestProducer.class,
					CDITestSupport.UserCatalogProducer.class)
			.activate(RequestScoped.class)
			.build();

	@Inject
	@Current
	Instance<User> userInstance;

	@AfterEach
	void clearRequest()
	{
		CDITestSupport.HttpServletRequestProducer.clear();
	}

	@Test
	void testReturnsEmptyUserWhenRequestHasNoAuthorization()
	{
		CDITestSupport.HttpServletRequestProducer.set(TestServletSupport.request().request());

		var user = userInstance.get();

		assertNull(user.getId());
	}

	@Test
	void testReturnsStatelessUserFromBearerToken() throws ServletException, IOException
	{
		var embeddedUser = new User()
				.setId(ID.valueOf(7))
				.setName("User")
				.setUsername("user")
				.setEmail("user@gate.test")
				.setRole(new Role().setId(ID.valueOf(3)).setName("Role").setRolename("role"));

		var token = Credentials.create(embeddedUser.getId(), null, embeddedUser).toString();
		var request = TestServletSupport.request(
				java.util.Map.of(),
				java.util.Map.of("Authorization", "Bearer " + token),
				null,
				"GET",
				"/Gate");

		new SlidingSessionFilter()
				.doFilter(request.request(), TestServletSupport.response().response(), TestServletSupport.chain().chain());

		CDITestSupport.HttpServletRequestProducer.set(request.request());

		var user = userInstance.get();

		assertEquals(embeddedUser.getId(), user.getId());
		assertEquals(embeddedUser.getUsername(), user.getUsername());
		var cachedUser = (User) request.attributes().get(User.class.getName());
		assertEquals(embeddedUser.getId(), cachedUser.getId());
		assertEquals(embeddedUser.getUsername(), cachedUser.getUsername());
	}
}
