package gate.thymeleaf;

import gate.constraint.Maxlength;
import gate.constraint.Required;
import gate.entity.Role;
import gate.entity.User;
import gate.type.Sex;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public class GateDialectTest
{

	@Test
	@Ignore
	public void test() throws URISyntaxException, IOException
	{
		TemplateEngine engine = new TemplateEngine();
		engine.addDialect(new GateDialect());
		StringTemplateResolver resolver = new StringTemplateResolver();
		resolver.setTemplateMode(TemplateMode.HTML);
		engine.setTemplateResolver(resolver);
		Context ctx = new Context();
		ctx.setVariable("screen", new GateDialectScreen());
		String template = Files.readString(Path.of(getClass().getResource("GateDialectTest.html").toURI()));
		String result = engine.process(template, ctx);

		String expected = Files.readString(Path.of(getClass().getResource("GateDialectTestResult.html").toURI()));

		Assert.assertEquals(expected, result);
	}

	public static class GateDialectScreen
	{

		@Required
		@Maxlength(32)
		private String name = "Gate Dialect";

		private Boolean alive = true;

		private boolean dead = false;

		private Sex sex = Sex.MALE;

		private final List<User> users = List.of(new User().setName("Role 1 User 1").setRole(new Role().setName("Role1")),
			new User().setName("Role 1 User 2").setRole(new Role().setName("Role1")),
			new User().setName("Role 2 User 1").setRole(new Role().setName("Role2")),
			new User().setName("Role 2 User 2").setRole(new Role().setName("Role12")));

		public Sex getSex()
		{
			return sex;
		}

		public void setSex(Sex sex)
		{
			this.sex = sex;
		}

		public Boolean getAlive()
		{
			return alive;
		}

		public void setDead(boolean dead)
		{
			this.dead = dead;
		}

		public boolean getDead()
		{
			return dead;
		}

		public List<User> getUsers()
		{
			return users;
		}

		public void setAlive(Boolean alive)
		{
			this.alive = alive;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public List<String> getOptions()
		{
			return List.of("option 1", "option 2", "option 3", "option 4", "option 5");
		}
	}
}
