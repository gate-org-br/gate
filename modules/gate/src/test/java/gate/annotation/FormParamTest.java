package gate.annotation;

import gate.http.ScreenServletRequest;
import gate.http.TestServletSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

class FormParamTest
{
	@Test
	void shouldExtractScalarFormParam() throws Exception
	{
		var request = request(Map.of("id", "123"));
		var result = FormParam.Extractor.extract(request, parameter("scalar", 0));

		Assertions.assertEquals(123, result);
	}

	@Test
	void shouldExtractComplexFormParamFromPrefixedProperties() throws Exception
	{
		var request = request(Map.of(
				"form.name", "Ana",
				"form.role.id", "7",
				"form.role.name", "Admin"));

		var result = (FormMock) FormParam.Extractor.extract(request, parameter("complex", 0));

		Assertions.assertEquals("Ana", result.getName());
		Assertions.assertEquals(7, result.getRole().getId());
		Assertions.assertEquals("Admin", result.getRole().getName());
	}

	@Test
	void shouldPreferDirectValueOverPrefixedProperties() throws Exception
	{
		var request = request(Map.of(
				"form", "value",
				"form.name", "Ana"));

		var result = FormParam.Extractor.extract(request, parameter("direct", 0));

		Assertions.assertEquals("value", result);
	}

	void scalar(@FormParam("id") Integer id)
	{
	}

	void complex(@FormParam("form") FormMock form)
	{
	}

	void direct(@FormParam("form") String form)
	{
	}

	private static ScreenServletRequest request(Map<String, String> parameters)
	{
		return new ScreenServletRequest(TestServletSupport.request(parameters,
				Map.of(), null, "POST", "/Gate").request());
	}

	private static Parameter parameter(String method, int index) throws NoSuchMethodException
	{
		for (Method candidate : FormParamTest.class.getDeclaredMethods())
			if (candidate.getName().equals(method))
				return candidate.getParameters()[index];
		throw new NoSuchMethodException(method);
	}

	public static class FormMock
	{
		private String name;
		private RoleMock role;

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public RoleMock getRole()
		{
			return role;
		}

		public void setRole(RoleMock role)
		{
			this.role = role;
		}
	}

	public static class RoleMock
	{
		private int id;
		private String name;

		public int getId()
		{
			return id;
		}

		public void setId(int id)
		{
			this.id = id;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}
	}
}
