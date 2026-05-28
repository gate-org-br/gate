package gate.base;

import gate.annotation.FormParam;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import gate.http.TestServletSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ScreenTest
{
	@Test
	void executeShouldReuseScreenPropertyForFormParam() throws Throwable
	{
		var screen = new FormScreen();
		var request = new ScreenServletRequest(TestServletSupport.request(Map.of(
				"form.name", "Ana",
				"orderBy", "name"), Map.of(), null, "POST", "/Gate").request());
		var response = new ScreenServletResponse(TestServletSupport.response().response());
		screen.prepare(request, response);
		var result = (FormMock) screen.execute(FormScreen.class.getMethod("call", FormMock.class));

		Assertions.assertSame(screen.getForm(), result);
		Assertions.assertEquals("name", screen.getOrderBy());
		Assertions.assertEquals("Ana", result.getName());
	}

	public static class FormScreen extends Screen
	{
		private FormMock form;

		public FormMock call(@FormParam("form") FormMock form)
		{
			return form;
		}

		public FormMock getForm()
		{
			return form;
		}

		public void setForm(FormMock form)
		{
			this.form = form;
		}
	}

	public static class FormMock
	{
		private String name;

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