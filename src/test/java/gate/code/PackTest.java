package gate.code;

import gate.entity.User;
import java.net.URISyntaxException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PackTest
{

	@Test
	public void testGetParent() throws URISyntaxException
	{
		PackageName pack = PackageName.of(User.class);
		assertEquals("gate", pack.getParent().toString());
	}

	@Test
	public void testResolve() throws URISyntaxException
	{
		PackageName pack = PackageName.of(User.class);
		assertEquals("gate.dao", pack.getParent().resolve("dao").toString());
	}

	@Test
	public void testResolveSibling() throws URISyntaxException
	{
		PackageName pack = PackageName.of(User.class);
		assertEquals("gate.dao", pack.resolveSibling("dao").toString());
	}

	@Test
	public void testSize() throws URISyntaxException
	{
		PackageName pack = PackageName.of(User.class);
		assertTrue(pack.size() == 2);
	}
}
