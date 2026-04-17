package gate.producer;

import gate.GateControl;
import gate.entity.User;
import gate.error.UnauthorizedException;
import gate.security.Credentials;
import gate.security.CryptoKeys;
import gate.test.TestServletObjects;
import gate.type.ID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class UserProducerTest
{
	private static class TestGateControl extends GateControl
	{
		User selected;

		@Override
		public User select(ID id)
		{
			return selected = new User().setId(id);
		}
	}

	@Test
	public void shouldReturnUserAlreadyStoredInRequest() throws Exception
	{
		UserProducer producer = new UserProducer();
		TestGateControl control = new TestGateControl();
		Credentials credentials = new Credentials(new CryptoKeys());
		TestServletObjects.RequestData data = new TestServletObjects.RequestData();
		User user = new User().setId(ID.valueOf(1));
		data.attributes.put(User.class.getName(), user);

		User result = producer.getUser(control, credentials, TestServletObjects.request(data));

		assertSame(user, result);
	}

	@Test
	public void shouldReturnEmptyUserWhenAuthorizationIsMissing() throws Exception
	{
		UserProducer producer = new UserProducer();

		User result = producer.getUser(new TestGateControl(), new Credentials(new CryptoKeys()),
				TestServletObjects.request(new TestServletObjects.RequestData()));

		assertEquals(null, result.getId());
	}

	@Test
	public void shouldResolveUserFromToken() throws Exception
	{
		UserProducer producer = new UserProducer();
		TestGateControl control = new TestGateControl();
		Credentials credentials = new Credentials(new CryptoKeys());
		String token = credentials.createToken(new Credentials.Subject(ID.valueOf(55), Instant.now()));
		TestServletObjects.RequestData data = new TestServletObjects.RequestData();
		data.headers.put("Authorization", "Bearer " + token);

		User result = producer.getUser(control, credentials, TestServletObjects.request(data));

		assertEquals(ID.valueOf(55), result.getId());
		assertEquals(result, data.attributes.get(User.class.getName()));
	}

	@Test
	public void shouldPropagateUnauthorizedExceptionForInvalidToken()
	{
		UserProducer producer = new UserProducer();
		TestServletObjects.RequestData data = new TestServletObjects.RequestData();
		data.headers.put("Authorization", "Bearer invalid.token.value");

		assertThrows(UnauthorizedException.class, () ->
				producer.getUser(new TestGateControl(), new Credentials(new CryptoKeys()),
						TestServletObjects.request(data)));
	}
}