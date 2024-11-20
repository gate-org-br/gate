package gate.lang.template;

import gate.entity.Role;
import gate.entity.User;
import gate.error.TemplateException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public final class TemplateTest
{

	private List<Role> roles;

	public TemplateTest()
	{
		getRoles().add(new Role().setName("Role 1"));
		getRoles().add(new Role().setName("Role 2"));
		getRoles().add(new Role().setName("Role 3"));

		getRoles().get(0).getUsers().add(new User().setName("User 1.1"));
		getRoles().get(0).getUsers().add(new User().setName("User 1.2"));
		getRoles().get(0).getUsers().add(new User().setName("User 1.3"));

		getRoles().get(1).getUsers().add(new User().setName("User 2.1"));
		getRoles().get(1).getUsers().add(new User().setName("User 2.2"));
		getRoles().get(1).getUsers().add(new User().setName("User 2.3"));

		getRoles().get(2).getUsers().add(new User().setName("User 3.1"));
		getRoles().get(2).getUsers().add(new User().setName("User 3.2"));
		getRoles().get(2).getUsers().add(new User().setName("User 3.3"));

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
	public void iteration() throws TemplateException, IOException
	{
		String expected = getFile("iteration/document.txt");
		Template template = Template.compile(getFile("iteration/template.txt"));
		String result = template.evaluate(List.of("Object 1", "Object 2", "Object 3"));
		assertEquals(expected, result);
	}

	@Test
	public void nestedIteration() throws TemplateException, IOException
	{
		String expected = getFile("nestedIteration/document.txt");
		Template template = Template.compile(getFile("nestedIteration/template.txt"));
		String result = template.evaluate(roles);
		assertEquals(expected, result);
	}

	@Test
	public void refNestedIteration() throws TemplateException, IOException
	{
		String expected = getFile("refNestedIteration/document.txt");
		Template template = Template.compile(getFile("refNestedIteration/template.txt"));
		String result = template.evaluate(roles);
		assertEquals(expected, result);
	}

	@Test
	public void indexedRefNestedIteration() throws TemplateException, IOException
	{
		String expected = getFile("indexedRefNestedIteration/document.txt");
		Template template = Template.compile(getFile("indexedRefNestedIteration/template.txt"));
		String result = template.evaluate(roles);
		assertEquals(expected, result);
	}

	@Test
	public void InvertedBlock() throws TemplateException, IOException
	{
		String expected = getFile("invertedBlock/document.txt");
		Template template = Template.compile(getFile("invertedBlock/template.txt"));
		String result = template.evaluate(roles);
		assertEquals(expected, result);
	}

	@Test
	public void URL() throws TemplateException, IOException
	{
		Template template = Template.compile("http://localhost:8000/employees/{{id}}/fire");
		String expected = "http://localhost:8000/employees/1234/fire";
		String result = template.evaluate(Map.of("id", "1234"));
		assertEquals(expected, result);
	}

	@Test
	public void template() throws TemplateException, IOException
	{
		String expected = getFile("Document.html");
		String result = Template.compile(getFile("Template.xml")).evaluate(this);
		assertEquals(expected, result);
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
