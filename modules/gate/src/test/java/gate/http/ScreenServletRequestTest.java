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

		var result = new CollectionFormMock();
		request.getPropertyGraph(CollectionFormMock.class)
				.populate(result, request::getParameter);

		Assertions.assertEquals(List.of(Permission.READ, Permission.WRITE), result.getPermissions());
	}

	@Test
	void shouldPopulateLeafArrayWithConvertedValues()
	{
		var request = new ScreenServletRequest(TestServletSupport.requestValues(
				Map.of("permissions[]", new String[]{"READ", "WRITE"}),
				Map.of(), null, "POST", "/Gate").request());

		var result = new ArrayFormMock();
		request.getPropertyGraph(ArrayFormMock.class)
				.populate(result, request::getParameter);

		Assertions.assertArrayEquals(new Permission[]{Permission.READ, Permission.WRITE}, result.getPermissions());
	}

	@Test
	void shouldPopulateIndexedArrayWithConvertedValues()
	{
		var request = new ScreenServletRequest(TestServletSupport.request(Map.of(
				"permissions[0]", "READ",
				"permissions[1]", "WRITE"),
				Map.of(), null, "POST", "/Gate").request());

		var result = new ArrayFormMock();
		request.getPropertyGraph(ArrayFormMock.class)
				.populate(result, request::getParameter);

		Assertions.assertArrayEquals(new Permission[]{Permission.READ, Permission.WRITE}, result.getPermissions());
	}

	public static class CollectionFormMock
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

	public static class ArrayFormMock
	{
		private Permission[] permissions;

		public Permission[] getPermissions()
		{
			return permissions;
		}

		public void setPermissions(Permission[] permissions)
		{
			this.permissions = permissions;
		}
	}

	enum Permission
	{
		READ, WRITE
	}
}
