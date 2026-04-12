package gate.producer;

import gate.entity.Role;
import gate.entity.User;
import gate.http.TestServletSupport;
import gate.security.Credentials;
import gate.type.ID;
import org.junit.jupiter.api.Test;

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
	void testReturnsStatelessUserFromBearerToken()
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

		var producer = new UserProducer();
		var user = producer.getUser(request.request());

		assertEquals(embeddedUser.getId(), user.getId());
		assertEquals(embeddedUser.getUsername(), user.getUsername());
		assertSame(user, request.attributes().get(User.class.getName()));
	}
}