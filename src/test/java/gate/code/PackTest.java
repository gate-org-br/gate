package gate.code;

import gate.code.PackageName;
import gate.entity.User;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;

public class PackTest
{

	@Test
	public void testGetParent() throws URISyntaxException
	{
		PackageName pack = PackageName.of(User.class);
		Assert.assertEquals("gate", pack.getParent().toString());
	}

	@Test
	public void testResolve() throws URISyntaxException
	{
		PackageName pack = PackageName.of(User.class);
		Assert.assertEquals("gate.dao", pack.getParent().resolve("dao").toString());
	}

	@Test
	public void testResolveSibling() throws URISyntaxException
	{
		PackageName pack = PackageName.of(User.class);
		Assert.assertEquals("gate.dao", pack.resolveSibling("dao").toString());
	}

	@Test
	public void testSize() throws URISyntaxException
	{
		PackageName pack = PackageName.of(User.class);
		Assert.assertTrue(pack.size() == 2);
	}
}
