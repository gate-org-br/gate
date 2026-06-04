package gate.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class ScreenServletRequestTest
{
	@Test
	void shouldPopulateLeafCollectionWithConvertedValues()
	{
		var request = new ScreenServletRequest(TestServletSupport.requestValues(
				Map.of("permissions[]", new String[]{"READ", "WRITE"}),
				Map.of(), null, "POST", "/Gate").request());

		var result = new FormMock();
		request.getPropertyGraph(FormMock.class)
				.populate(result, request::getParameter);

		Assertions.assertEquals(List.of(Permission.READ, Permission.WRITE), result.getPermissions());
	}

	public static class FormMock
	{
		private List<Permission> permissions;

		public List<Permission> getPermissions()
		{
			return permissions;
		}

		public void setPermissions(List<Permission> permissions)
		{
			this.permissions = permissions;
		}
	}

	enum Permission
	{
		READ, WRITE
	}
}
