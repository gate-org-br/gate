package gateclient;

import gate.entity.User;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;

public class PackTest
{
	
	@Test
	public void testGetParent() throws URISyntaxException
	{
		Pack pack = Pack.of(User.class);
		Assert.assertEquals("gate", pack.getParent().toString());
	}
	
	@Test
	public void testResolve() throws URISyntaxException
	{
		Pack pack = Pack.of(User.class);
		Assert.assertEquals("gate.dao", pack.getParent().resolve("dao").toString());
	}
	
	@Test
	public void testResolveSibling() throws URISyntaxException
	{
		Pack pack = Pack.of(User.class);
		Assert.assertEquals("gate.dao", pack.resolveSibling("dao").toString());
	}
	
	@Test
	public void testSize() throws URISyntaxException
	{
		Pack pack = Pack.of(User.class);
		Assert.assertTrue(pack.size() == 2);
	}
	
	@Test
	public void testProjectClasses() throws URISyntaxException
	{
		Pack pack = Pack.of(User.class);
		Path result = pack.getProject().getBinaryPath();
		Assert.assertTrue(result.endsWith(Path.of("gate").resolve("target").resolve("classes")));
	}
	
	@Test
	public void testProjectSources() throws URISyntaxException
	{
		Pack pack = Pack.of(User.class);
		Path result = pack.getProject().getSourcePath();
		Assert.assertTrue(result.endsWith(Path.of("gate").resolve("src").resolve("main").resolve("java")));
	}
}
