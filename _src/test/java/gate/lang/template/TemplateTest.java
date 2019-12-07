package gate.lang.template;

import gate.entity.Role;
import gate.entity.User;
import gate.error.TemplateException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
	public void template() throws TemplateException, IOException
	{
		String expected = getFile("Document.html");
		String result = Template.compile(getFile("Template.xml")).evaluate(this);
		Assert.assertEquals(expected, result);
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
