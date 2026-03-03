package gate.annotation;

import gate.entity.User;
import gate.type.ID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Entity$ExtractorTest
{

	@Test
	void shouldReturnPropertyReference()
	{
		User user = new User();
		user.setId(ID.valueOf(1));

		var ref = Entity.Extractor.get(User.class);
		assertEquals(user.getId(), ref.apply(user));
	}

	@Test
	void shouldReturnId()
	{
		User user = new User();
		user.setId(ID.valueOf(1));
		assertEquals(user.getId(), Entity.Extractor.extract(user));
	}

	@Test
	void shouldReturnNullWhenObjectIsNull()
	{
		assertEquals(null, Entity.Extractor.extract(null));
	}
}
