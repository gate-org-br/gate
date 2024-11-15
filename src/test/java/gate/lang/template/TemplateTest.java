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
		assertEquals(expected, result);
	}

	@Test
	public void forTest() throws TemplateException, IOException
	{
		Template template = Template.compile("""
					##for(value : {{this}})
					{{@value}}
					##end
					""");
		String expected = """
					Object 1
					Object 2
					Object 3
                    
                    """;

		String result = template.evaluate(List.of("Object 1", "Object 2", "Object 3"));

		assertEquals(expected, result);
	}

	@Test
	public void forIndexTest() throws TemplateException, IOException
	{
		Template template = Template.compile("""
					##for(value : {{this}} : index)
					{{@index + 1}}: {{@value}}
					##end
					""");
		String expected = """
					1: Object 1
					2: Object 2
					3: Object 3
                    
					""";

		String result = template.evaluate(List.of("Object 1", "Object 2", "Object 3"));

		assertEquals(expected, result);
	}

	@Test
	public void forIndexedElseTest() throws TemplateException, IOException
	{
		Template template = Template.compile("""
					##for(value : {{this}} : index)
					{{@index + 1}}: {{@value}}
					##else
					nothing
					##end
					""");

		String expected = """
					1: Object 1
					2: Object 2
					3: Object 3
                    
					""";

		String result = template.evaluate(List.of("Object 1", "Object 2", "Object 3"));

		assertEquals(expected, result);
	}

	@Test
	public void forIndexedElseEmptyTest() throws TemplateException, IOException
	{
		Template template = Template.compile("""
					##for(value : {{this}} : index)
					{{@index + 1}}: {{@value}}
					##else
					nothing
					##end
					""");

		String result = template.evaluate(List.of());
		System.out.println(result.replaceAll("\n", "N").replaceAll(" ", "S"));

		assertEquals("nothing\n\n", result);
	}

	@Test
	public void templateIfTrue() throws TemplateException, IOException
	{
		Template template = Template.compile("""
					##if({{this eq 'abc'}})
					condition true
					##end
					""");

		assertEquals("condition true\n", template.evaluate("abc"));
	}

	@Test
	public void templateIfFalse() throws TemplateException, IOException
	{
		Template template = Template.compile("""
					##if({{this eq 'abc'}})
					condition true
					##end
					""");

		assertEquals("", template.evaluate("cde"));
	}

	@Test
	public void templateIfTrueElse() throws TemplateException, IOException
	{
		Template template = Template.compile("""
					##if( {{this eq 'abc'}} )
					condition true
					##else
					condition false
					##end
					""");

		assertEquals("condition true\n", template.evaluate("abc"));
	}

	@Test
	public void templateIfFalseElse() throws TemplateException, IOException
	{
		Template template = Template.compile("""
					##if( {{this eq 'abc'}} )
					condition true
					##else
					condition false
					##end
					""");

		assertEquals("condition false\n", template.evaluate("cde"));
	}

	@Test
	public void URL() throws TemplateException, IOException
	{
		Template template = Template.compile("http://localhost:8000/employees/{{id}}/fire");
		String expected = "http://localhost:8000/employees/1234/fire";
		String result = template.evaluate(Map.of("id", "1234"));
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
