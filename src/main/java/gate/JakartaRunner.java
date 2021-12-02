package gate;

import gate.base.Screen;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.jboss.weld.context.api.ContextualInstance;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundLiteral;
import org.jboss.weld.context.bound.BoundRequest;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.context.bound.BoundSessionContext;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.jboss.weld.manager.api.WeldManager;
import org.slf4j.Logger;

public class JakartaRunner implements Runner
{

	@Inject
	private Logger logger;

	@Resource
	private ManagedThreadFactory managedThreadFactory;

	@Override
	public void execute(Progress progress, HttpServletRequest request, Screen screen, Method method)
	{

		Map<Class<? extends Annotation>, Collection<ContextualInstance<?>>> scopeToContextualInstances = new HashMap<>();
		CDI.current().select(WeldManager.class).get().getActiveWeldAlterableContexts()
			.forEach(context -> scopeToContextualInstances.put(context.getScope(), context.getAllContextualInstances()));

		managedThreadFactory.newThread(() ->
		{
			Progress.bind(progress);
			try
			{
				Thread.sleep(1000);

				WeldManager weldManager = CDI.current().select(WeldManager.class).get();
				BoundRequestContext requestContext = weldManager.instance().select(BoundRequestContext.class, BoundLiteral.INSTANCE).get();
				BoundSessionContext sessionContext = weldManager.instance().select(BoundSessionContext.class, BoundLiteral.INSTANCE).get();
				BoundConversationContext conversationContext = weldManager.instance().select(BoundConversationContext.class, BoundLiteral.INSTANCE).get();

				Map<String, Object> sessionMap = new HashMap<>();
				Map<String, Object> requestMap = new HashMap<>();
				BoundRequest boundRequest = new MutableBoundRequest(requestMap, sessionMap);

				requestContext.associate(requestMap);
				requestContext.activate();
				sessionContext.associate(sessionMap);
				sessionContext.activate();
				conversationContext.associate(boundRequest);
				conversationContext.activate();

				if (scopeToContextualInstances.get(requestContext.getScope()) != null)
					requestContext.clearAndSet(scopeToContextualInstances.get(requestContext.getScope()));
				if (scopeToContextualInstances.get(sessionContext.getScope()) != null)
					sessionContext.clearAndSet(scopeToContextualInstances.get(sessionContext.getScope()));
				if (scopeToContextualInstances.get(conversationContext.getScope()) != null)
					conversationContext.clearAndSet(scopeToContextualInstances.get(conversationContext.getScope()));

				Object url = screen.execute(method);

				requestContext.deactivate();
				conversationContext.deactivate();
				sessionContext.deactivate();

				if (url != null)
					Progress.redirect(url.toString());
			} catch (InvocationTargetException ex)
			{
				if (Progress.Status.PENDING.equals(Progress.status()))
					Progress.cancel(ex.getCause().getMessage());
				else
					Progress.message(ex.getCause().getMessage());
			} catch (RuntimeException | IllegalAccessException | InterruptedException ex)
			{
				if (Progress.Status.PENDING.equals(Progress.status()))
					Progress.cancel(ex.getMessage());
				else
					Progress.message(ex.getMessage());
				logger.error(ex.getMessage(), ex);
			} finally
			{
				try
				{
					Thread.sleep(5000);
					Progress.dispose();
				} catch (InterruptedException ex)
				{
					Progress.dispose();
				}
			}
		}).start();
	}
}
