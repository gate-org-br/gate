package gate;

import gate.annotation.Alert;
import gate.annotation.Annotations;
import gate.annotation.Color;
import gate.annotation.Confirm;
import gate.annotation.Description;
import gate.annotation.Disabled;
import gate.annotation.Emoji;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Public;
import gate.annotation.Superuser;
import gate.annotation.Tooltip;
import gate.base.Screen;
import gate.entity.User;
import gate.error.InvalidRequestException;
import gate.util.Emojis;
import gate.util.Icons;
import gate.util.Toolkit;
import java.lang.reflect.Method;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public class Call
{

	private final String module;
	private final String screen;
	private final String action;
	private final Class<Screen> type;
	private final Method method;

	public Call(String module, String screen, String action, Class<Screen> type, Method method)
	{
		this.module = module;
		this.screen = screen;
		this.action = action;
		this.type = type;
		this.method = method;
	}

	public static Call of(String module, String screen, String action) throws InvalidRequestException
	{
		Class<Screen> type = Screen.getScreen(module, screen).orElseThrow(() -> new InvalidRequestException(module, screen, action));
		Method method = Screen.getAction(type, action).orElseThrow(() -> new InvalidRequestException(module, screen, action));
		return new Call(module, screen, action, type, method);
	}

	public static Call of(HttpServletRequest request, String module, String screen, String action) throws InvalidRequestException
	{

		if ("#".equals(module))
			module = request.getParameter("MODULE");
		if ("#".equals(screen))
			screen = request.getParameter("SCREEN");
		if ("#".equals(action))
			action = request.getParameter("ACTION");

		if (Toolkit.isEmpty(module))
		{
			module = request.getParameter("MODULE");
			if (Toolkit.isEmpty(screen))
			{
				screen = request.getParameter("SCREEN");
				if (Toolkit.isEmpty(action))
					action = request.getParameter("ACTION");
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

	public Optional<Icons.Icon> getIcon()
	{
		return action != null ? Icon.Extractor.extract(method) : Icon.Extractor.extract(method).or(() -> Icon.Extractor.extract(type));
	}

	public Optional<Emojis.Emoji> getEmoji()
	{
		return action != null ? Emoji.Extractor.extract(method) : Emoji.Extractor.extract(method).or(() -> Emoji.Extractor.extract(type));
	}

	public Optional<String> getName()
	{
		return action != null ? Name.Extractor.extract(method) : Name.Extractor.extract(method).or(() -> Name.Extractor.extract(type));
	}

	public Optional<String> getDescription()
	{
		return action != null ? Description.Extractor.extract(method) : Description.Extractor.extract(method).or(() -> Description.Extractor.extract(type));
	}

	public Optional<String> getTooltip()
	{
		return action != null ? Tooltip.Extractor.extract(method) : Tooltip.Extractor.extract(method).or(() -> Tooltip.Extractor.extract(type));
	}

	public Optional<String> getColor()
	{
		return action != null ? Color.Extractor.extract(method) : Color.Extractor.extract(method).or(() -> Color.Extractor.extract(type));
	}

	public Optional<String> getConfirm()
	{
		return action != null ? Confirm.Extractor.extract(method) : Confirm.Extractor.extract(method).or(() -> Confirm.Extractor.extract(type));
	}

	public Optional<String> getAlert()
	{
		return action != null ? Alert.Extractor.extract(method) : Alert.Extractor.extract(method).or(() -> Alert.Extractor.extract(type));
	}

	public Class<Screen> getType()
	{
		return type;
	}

	public Method getMethod()
	{
		return method;
	}

	public boolean checkAccess(User user)
	{
		if (Annotations.exists(Disabled.class, type, method))
			return false;

		if (user != null && user.isSuperUser())
			return true;

		if (Annotations.exists(Superuser.class, type, method))
			return user != null && user.isSuperUser();

		if (Annotations.exists(Public.class, type, method))
			return user == null || !user.checkBlock(module, screen, action);

		return user != null && user.checkAccess(module, screen, action);
	}

}
