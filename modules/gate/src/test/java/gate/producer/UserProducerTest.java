package gate.producer;

import gate.SlidingSessionFilter;
import gate.entity.Role;
import gate.entity.User;
import gate.http.TestServletSupport;
import gate.security.Credentials;
import gate.type.ID;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UserProducerTest
{
	@Test
	void testReturnsEmptyUserWhenRequestHasNoAuthorization()
	{
		var producer = new UserProducer();
		var user = producer.getUser(TestServletSupport.request().request());

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

		var producer = new UserProducer();
		var user = producer.getUser(request.request());

		assertEquals(embeddedUser.getId(), user.getId());
		assertEquals(embeddedUser.getUsername(), user.getUsername());
		assertSame(user, request.attributes().get(User.class.getName()));
	}
}