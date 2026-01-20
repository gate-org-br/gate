package gate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import org.thymeleaf.web.IWebExchange;

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
import gate.type.RequestCommand;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;

public record Call(RequestCommand command,
	Class<Screen> type,
	Method method)
	{

	private static List<Class<? extends Annotation>> METHODS
		= List.of(GET.class,
			POST.class,
			PUT.class,
			PATCH.class,
			HEAD.class,
			DELETE.class,
			OPTIONS.class);

	public static final Call NONE = new Call();

	private Call()
	{
		this(null, null, null);
	}

	public static Call of(Method method) throws BadRequestException
	{
		@SuppressWarnings("unchecked")
		Class<Screen> type = (Class<Screen>) method.getDeclaringClass();
		String module = type.getPackageName();
		String screen
			= type.getSimpleName().equals("Screen") ? null : type.getSimpleName().substring(6);
		String action = method.getName().equals("call") ? null : method.getName().substring(4);
		return new Call(new RequestCommand(module, screen, action), type, method);
	}

	public static Call of(RequestCommand command) throws BadRequestException
	{
		if (command.equals(RequestCommand.DEFAULT))
			return NONE;

		Class<Screen> type = Screen.getScreen(command.module(), command.screen())
			.orElseThrow(() -> new BadRequestException(command));
		if (Modifier.isAbstract(type.getModifiers()))
			throw new BadRequestException(command);
		Method method = Screen.getAction(type, command.action())
			.orElseThrow(() -> new BadRequestException(command));
		return new Call(command, type, method);
	}

	public static Call of(HttpServletRequest request, String module, String screen, String action)
		throws BadRequestException
	{
		return of(new RequestCommand(request.getParameter("MODULE"),
			request.getParameter("SCREEN"),
			request.getParameter("ACTION"))
			.with(module, screen, action));
	}

	public static Call of(IWebExchange exchange, String module, String screen, String action)
		throws BadRequestException
	{
		return of(new RequestCommand(exchange.getRequest().getParameterValue("MODULE"),
			(String) exchange.getRequest().getParameterValue("SCREEN"),
			(String) exchange.getRequest().getParameterValue("ACTION"))
			.with(module, screen, action));
	}

	public static Call of(IWebExchange exchange) throws BadRequestException
	{
		return of(new RequestCommand((String) exchange.getAttributeValue("MODULE"),
			(String) exchange.getAttributeValue("SCREEN"),
			(String) exchange.getAttributeValue("ACTION")));
	}

	public Optional<gate.icon.Icon> getIcon()
	{
		return Icon.Extractor.extract(method).or(() -> Icon.Extractor.extract(type));
	}

	public Optional<gate.icon.Emoji> getEmoji()
	{
		return Emoji.Extractor.extract(method).or(() -> Emoji.Extractor.extract(type));
	}

	public Optional<String> getName()
	{
		return Name.Extractor.extract(method).or(() -> Name.Extractor.extract(type));
	}

	public Optional<String> getDescription()
	{
		return Description.Extractor.extract(method).or(() -> Description.Extractor.extract(type));
	}

	public Optional<String> getTooltip()
	{
		return Tooltip.Extractor.extract(method).or(() -> Tooltip.Extractor.extract(type));
	}

	public Optional<String> getColor()
	{
		return Color.Extractor.extract(method).or(() -> Color.Extractor.extract(type));
	}

	public Optional<String> getConfirm()
	{
		return Confirm.Extractor.extract(method).or(() -> Confirm.Extractor.extract(type));
	}

	public Optional<String> getAlert()
	{
		return Alert.Extractor.extract(method).or(() -> Alert.Extractor.extract(type));
	}

	public boolean isPublic()
	{
		return Annotations.exists(Public.class, type, method);
	}

	public boolean checkMethod(String method)
	{
		return METHODS.stream()
			.filter(method()::isAnnotationPresent)
			.count() == 0
			|| METHODS.stream()
				.filter(method()::isAnnotationPresent)
				.map(Class::getSimpleName)
				.anyMatch(e -> e.equalsIgnoreCase(method));

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
			return user != null && user.getId() != null && user.isSuperUser();

		return switch (Security.Extractor.extract(method)
			.orElse(Security.Type.AUTHORIZATION))
		{
			case NONE ->
				true;
			case AUTHENTICATION ->
				user != null && user.getId() != null;
			case AUTHORIZATION ->
			{
				var auth = Authorization.Extractor.extract(method, command.module(), command.screen(), command.action());
				yield user != null && user.getId() != null
				&& user.checkAccess(auth.module(), auth.screen(), auth.action());
			}
			case SPECIFIC_AUTHORIZATION ->
			{
				var auth = Authorization.Extractor.extract(method, command.module(), command.screen(), command.action());
				yield user != null
				&& user.getId() != null
				&& user.checkSpecificAccess(auth.module(), auth.screen(), auth.action());
			}
			case SUPERUSER ->
				user != null
				&& user.getId() != null
				&& user.isSuperUser();
		};
	}

	@Override
	public String toString()
	{
		StringJoiner joiner = new StringJoiner("&");

		if (command.module() != null && !command.module().isBlank())
			joiner.add("MODULE=" + command.module());

		if (command.screen() != null && !command.screen().isBlank())
			joiner.add("SCREEN=" + command.screen());

		if (command.action() != null && !command.action().isBlank())
			joiner.add("ACTION=" + command.action());

		String query = joiner.toString();
		return query.isEmpty() ? "Gate" : "Gate?" + query;
	}
}
