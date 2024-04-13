package gate;

import gate.annotation.Alert;
import gate.annotation.Annotations;
import gate.annotation.Authorization;
import gate.annotation.Color;
import gate.annotation.Confirm;
import gate.annotation.Description;
import gate.annotation.Disabled;
import gate.annotation.Emoji;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Public;
import gate.annotation.Security;
import gate.annotation.Superuser;
import gate.annotation.Tooltip;
import gate.base.Screen;
import gate.entity.User;
import gate.error.BadRequestException;
import gate.util.Toolkit;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import org.thymeleaf.web.IWebExchange;

public class Call
{

	private final String module;
	private final String screen;
	private final String action;
	private final Class<Screen> type;
	private final Method method;

	public static final Call NONE = new Call(null, null, null, null, null);

	private Call(String module, String screen, String action, Class<Screen> type, Method method)
	{
		this.type = type;
		this.method = method;
		this.module = module;
		this.screen = screen;
		this.action = action;
	}

	public static Call of(Method method) throws BadRequestException
	{
		@SuppressWarnings("unchecked")
		Class<Screen> type = (Class<Screen>) method.getDeclaringClass();
		String module = type.getPackageName();
		String screen =
				type.getSimpleName().equals("Screen") ? null : type.getSimpleName().substring(6);
		String action = method.getName().equals("call") ? null : method.getName().substring(4);
		return new Call(module, screen, action, type, method);
	}

	public static Call of(String module, String screen, String action) throws BadRequestException
	{
		Class<Screen> type = Screen.getScreen(module, screen)
				.orElseThrow(() -> new BadRequestException(module, screen, action));
		if (Modifier.isAbstract(type.getModifiers()))
			throw new BadRequestException(module, screen, action);
		Method method = Screen.getAction(type, action)
				.orElseThrow(() -> new BadRequestException(module, screen, action));
		return new Call(module, screen, action, type, method);
	}

	public static Call of(HttpServletRequest request, String module, String screen, String action)
			throws BadRequestException
	{

		if ("#".equals(module))
			module = (String) request.getAttribute("MODULE");
		if ("#".equals(screen))
			screen = (String) request.getAttribute("SCREEN");
		if ("#".equals(action))
			action = (String) request.getAttribute("ACTION");

		if (Toolkit.isEmpty(module))
		{
			module = (String) request.getAttribute("MODULE");
			if (Toolkit.isEmpty(screen))
			{
				screen = (String) request.getAttribute("SCREEN");
				if (Toolkit.isEmpty(action))
					action = (String) request.getAttribute("ACTION");
			}
		}

		return of(module, screen, action);
	}

	public static Call of(IWebExchange exchange, String module, String screen, String action)
			throws BadRequestException
	{

		if ("#".equals(module))
			module = (String) exchange.getAttributeValue("MODULE");
		if ("#".equals(screen))
			screen = (String) exchange.getAttributeValue("SCREEN");
		if ("#".equals(action))
			action = (String) exchange.getAttributeValue("ACTION");

		if (Toolkit.isEmpty(module))
		{
			module = (String) exchange.getAttributeValue("MODULE");
			if (Toolkit.isEmpty(screen))
			{
				screen = (String) exchange.getAttributeValue("SCREEN");
				if (Toolkit.isEmpty(action))
					action = (String) exchange.getAttributeValue("ACTION");
			}
		}

		return of(module, screen, action);
	}

	public String getModule()
	{
		return module;
	}

	public String getScreen()
	{
		return screen;
	}

	public String getAction()
	{
		return action;
	}

	public Optional<gate.icon.Icon> getIcon()
	{
		return action != null ? Icon.Extractor.extract(method)
				: Icon.Extractor.extract(method).or(() -> Icon.Extractor.extract(type));
	}

	public Optional<gate.icon.Emoji> getEmoji()
	{
		return action != null ? Emoji.Extractor.extract(method)
				: Emoji.Extractor.extract(method).or(() -> Emoji.Extractor.extract(type));
	}

	public Optional<String> getName()
	{
		return action != null ? Name.Extractor.extract(method)
				: Name.Extractor.extract(method).or(() -> Name.Extractor.extract(type));
	}

	public Optional<String> getDescription()
	{
		return action != null ? Description.Extractor.extract(method)
				: Description.Extractor.extract(method)
						.or(() -> Description.Extractor.extract(type));
	}

	public Optional<String> getTooltip()
	{
		return action != null ? Tooltip.Extractor.extract(method)
				: Tooltip.Extractor.extract(method).or(() -> Tooltip.Extractor.extract(type));
	}

	public Optional<String> getColor()
	{
		return action != null ? Color.Extractor.extract(method)
				: Color.Extractor.extract(method).or(() -> Color.Extractor.extract(type));
	}

	public Optional<String> getConfirm()
	{
		return action != null ? Confirm.Extractor.extract(method)
				: Confirm.Extractor.extract(method).or(() -> Confirm.Extractor.extract(type));
	}

	public Optional<String> getAlert()
	{
		return action != null ? Alert.Extractor.extract(method)
				: Alert.Extractor.extract(method).or(() -> Alert.Extractor.extract(type));
	}

	public Class<Screen> getType()
	{
		return type;
	}

	public Method getMethod()
	{
		return method;
	}

	public boolean isPublic()
	{
		return Annotations.exists(Public.class, type, method);
	}

	public boolean checkAccess(User user)
	{

		if (Annotations.exists(Disabled.class, type, method))
			return false;

		if (user != null && user.isSuperUser())
			return true;

		if (Annotations.exists(Public.class, type, method))
			return true;

		if (Annotations.exists(Superuser.class, type, method))
			return user != null && user.isSuperUser();

		return switch (Security.Extractor.extract(method).orElse(Security.Type.AUTHORIZATION))
		{
			case NONE -> true;
			case AUTHENTICATION -> user != null;
			case AUTHORIZATION -> {
				var auth = Authorization.Extractor.extract(method, module, screen, action);
				yield user != null && user.checkAccess(auth.module(), auth.screen(), auth.action());
			}
			case SPECIFIC_AUTHORIZATION -> {
				var auth = Authorization.Extractor.extract(method, module, screen, action);
				yield user != null
						&& user.checkSpecificAccess(auth.module(), auth.screen(), auth.action());
			}
			case SUPERUSER -> user != null && user.isSuperUser();
			default -> throw new IllegalStateException();
		};
	}
}
