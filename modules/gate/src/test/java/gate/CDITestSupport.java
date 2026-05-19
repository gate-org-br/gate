package gate;

import gate.catalog.UserCatalog;
import gate.type.ID;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.servlet.http.HttpServletRequest;

public final class CDITestSupport
{
	private CDITestSupport()
	{
	}

	public static class HttpServletRequestProducer
	{
		private static final ThreadLocal<HttpServletRequest> REQUEST = new ThreadLocal<>();

		public static void set(HttpServletRequest request)
		{
			REQUEST.set(request);
		}

		public static void clear()
		{
			REQUEST.remove();
		}

		@Produces
		@Dependent
		public HttpServletRequest getRequest()
		{
			var request = REQUEST.get();
			if (request == null)
				throw new IllegalStateException("No HttpServletRequest configured for CDI test");
			return request;
		}
	}

	public static class UserCatalogProducer
	{
		@Produces
		@Dependent
		public UserCatalog getUserCatalog()
		{
			return new UserCatalog()
			{
				@Override
				public gate.entity.User select(ID id)
				{
					throw new UnsupportedOperationException("Stateful user lookup is not configured for this CDI test");
				}
			};
		}
	}
}