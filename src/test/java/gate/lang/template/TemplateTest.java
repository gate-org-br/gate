package gate.lang.template;

import gate.error.TemplateException;
import gate.entity.Role;
import gate.entity.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

public final class TemplateTest
{

	private List<Role> roles;

	public TemplateTest()
	{
		getRoles().add(new Role().setName("Role 1"));
		getRoles().add(new Role().setName("Role 2"));
		getRoles().add(new Role().setName("Role 3"));

		getRoles().get(0).getUsers().add(new User().setName("User 0.1"));
		getRoles().get(0).getUsers().add(new User().setName("User 0.2"));
		getRoles().get(0).getUsers().add(new User().setName("User 0.3"));

		getRoles().get(1).getUsers().add(new User().setName("User 1.1"));
		getRoles().get(1).getUsers().add(new User().setName("User 1.2"));
		getRoles().get(1).getUsers().add(new User().setName("User 1.3"));

		getRoles().get(2).getUsers().add(new User().setName("User 2.1"));
		getRoles().get(2).getUsers().add(new User().setName("User 2.2"));
		getRoles().get(2).getUsers().add(new User().setName("User 2.3"));

	}

	private String getFile(String file) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file))))
		{
			StringBuilder string = new StringBuilder();
			for (int c = reader.read(); c != -1; c = reader.read())
				string.append((char) c);
			return string.toString();
		}
	}

	@Test
	public void templateContext() throws TemplateException
	{
		try
		{
			Assert.assertEquals(getFile("Document.html"), Template.evaluate(this, getFile("TemplateContext.xml")));
		} catch (IOException ex)
		{
			Logger.getLogger(TemplateTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void templateVariable() throws TemplateException
	{
		try
		{
			Assert.assertEquals(getFile("Document.html"), Template.evaluate(this, getFile("TemplateVariable.xml")));
		} catch (IOException ex)
		{
			Logger.getLogger(TemplateTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public List<Role> getRoles()
	{
		if (roles == null)
			roles = new ArrayList<>();
		return roles;
	}

	public void setRoles(List<Role> roles)
	{
		this.roles = roles;
	}
}
