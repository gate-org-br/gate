package gate.entity;

import gate.annotation.Converter;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.converter.AppConverter;
import gate.error.ConversionException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.Dependent;

@Dependent
@Icon("2189")
@Converter(AppConverter.class)
public class App implements Serializable
{

	private App()
	{
	}

	private String id;
	private String name;
	private String description;
	private List<Module> modules;

	private static final long serialVersionUID = 1L;

	public String getId()
	{
		return id;
	}

	public String getDescription()
	{
		return description;
	}

	public String getName()
	{
		return name;
	}

	public List<Module> getModules()
	{
		return modules;
	}

	public static App getInstance(String id, String name, String description,
		List<Class<? extends Screen>> screens)
	{
		App app = new App();
		app.id = id;
		app.name = name;
		app.description = description;

		app.modules = screens.stream()
			.filter(type -> type.getSimpleName().length() > 6)
			.filter(type -> type.getSimpleName().endsWith("Screen"))
			.filter(type -> !Modifier.isAbstract(type.getModifiers()))
			.map(type -> type.getPackage()).distinct().map(pack ->
		{
			Module module = new Module();
			module.id = pack.getName();

			screens.stream()
				.filter(type -> !Modifier.isAbstract(type.getModifiers()))
				.filter(type -> type.getSimpleName().equals("Screen")).findAny().ifPresent(type ->
			{
				Name.Extractor.extract(type).ifPresent(e -> module.name = e);
				Description.Extractor.extract(type).ifPresent(e -> module.description = e);
				Icon.Extractor.extract(type).ifPresent(e -> module.icon = e.getCode());
			});

			module.screens = screens.stream()
				.filter(e -> e.getPackage().getName().equals(pack.getName()))
				.filter(e -> e.getSimpleName().endsWith("Screen"))
				.map(Module.Screen::of)
				.collect(Collectors.toList());

			return module;
		}).collect(Collectors.toList());

		return app;
	}

	public JsonObject toJsonObject()
	{
		return new JsonObject()
			.setString("id", id)
			.setString("name", name)
			.setString("description", description)
			.set("modules", modules.stream().map(Module::toJsonObject)
				.collect(Collectors.toCollection(() -> new JsonArray())));

	}

	@Override
	public String toString()
	{
		return toJsonObject().toString();

	}

	public static App of(String string) throws ConversionException
	{
		return of(JsonObject.parse(string));
	}

	public static App of(JsonObject jsonObject)
	{
		var app = new App();
		jsonObject.getString("id").ifPresent(e -> app.id = e);
		jsonObject.getString("name").ifPresent(e -> app.name = e);
		jsonObject.getString("description").ifPresent(e -> app.description = e);

		jsonObject.getJsonArray("modules").ifPresent(jsonArray
			-> app.modules = jsonArray.stream()
				.filter(e -> e instanceof JsonObject)
				.map(e -> (JsonObject) e)
				.map(Module::of)
				.collect(Collectors.toList()));

		return app;
	}

	@Icon("2006")
	public static class Module implements Serializable
	{

		private static final long serialVersionUID = 1L;

		private String id;
		private String icon;
		private String name;
		private String description;
		private List<Screen> screens;

		public String getId()
		{
			return id;
		}

		public String getName()
		{
			return name;
		}

		public String getDescription()
		{
			return description;
		}

		public List<Screen> getScreens()
		{
			return screens;
		}

		public String getIcon()
		{
			return icon;
		}

		public Screen addScreen()
		{
			Screen screen = new Screen();
			screens.add(screen);
			return screen;
		}

		public JsonObject toJsonObject()
		{
			return new JsonObject()
				.setString("id", id)
				.setString("name", name)
				.setString("icon", icon)
				.setString("description", description)
				.set("screens", screens.stream().map(Screen::toJsonObject)
					.collect(Collectors.toCollection(() -> new JsonArray())));

		}

		@Override
		public String toString()
		{
			return toJsonObject().toString();

		}

		private static Module of(JsonObject jsonObject)
		{
			var module = new Module();
			jsonObject.getString("id").ifPresent(e -> module.id = e);
			jsonObject.getString("name").ifPresent(e -> module.name = e);
			jsonObject.getString("icon").ifPresent(e -> module.icon = e);
			jsonObject.getString("description").ifPresent(e -> module.description = e);

			jsonObject.getJsonArray("screens").ifPresent(jsonArray
				-> module.screens = jsonArray.stream()
					.filter(e -> e instanceof JsonObject)
					.map(e -> (JsonObject) e)
					.map(Screen::of)
					.collect(Collectors.toList()));

			return module;
		}

		@Icon("2044")
		public static class Screen implements Serializable
		{

			private static final long serialVersionUID = 1L;

			private String id;
			private String name;
			private String icon;
			private String description;
			private List<Action> actions;

			public String getId()
			{
				return id;
			}

			public String getName()
			{
				return name;
			}

			public List<Action> getActions()
			{
				return actions;
			}

			public String getDescription()
			{
				return description;
			}

			public String getIcon()
			{
				return icon;
			}

			public JsonObject toJsonObject()
			{
				return new JsonObject()
					.setString("id", id)
					.setString("name", name)
					.setString("icon", icon)
					.setString("description", description)
					.set("actions", actions.stream().map(Action::toJsonObject)
						.collect(Collectors.toCollection(() -> new JsonArray())));

			}

			@Override
			public String toString()
			{
				return toJsonObject().toString();

			}

			private static Screen of(Class<?> type)
			{
				var screen = new Screen();
				if (type.getEnclosingClass() != null)
					screen.id = type.getEnclosingClass().getSimpleName() + "$"
						+ type.getSimpleName().substring(0, type.getSimpleName().length() - 6);
				else
					screen.id = type.getSimpleName().substring(0, type.getSimpleName().length() - 6);

				Name.Extractor.extract(type).ifPresent(e -> screen.name = e);
				Description.Extractor.extract(type).ifPresent(e -> screen.description = e);
				Icon.Extractor.extract(type).ifPresent(e -> screen.icon = e.getCode());
				screen.actions = Stream.of(type.getMethods())
					.filter(e -> e.getName().length() > 4 && e.getName().startsWith("call"))
					.map(Screen.Action::of)
					.collect(Collectors.toList());

				return screen;
			}

			private static Screen of(JsonObject jsonObject)
			{
				var screen = new Screen();
				jsonObject.getString("id").ifPresent(e -> screen.id = e);
				jsonObject.getString("name").ifPresent(e -> screen.name = e);
				jsonObject.getString("icon").ifPresent(e -> screen.icon = e);
				jsonObject.getString("description").ifPresent(e -> screen.description = e);

				jsonObject.getJsonArray("actions").ifPresent(jsonArray
					-> screen.actions = jsonArray.stream()
						.filter(e -> e instanceof JsonObject)
						.map(e -> (JsonObject) e)
						.map(Action::of)
						.collect(Collectors.toList()));

				return screen;
			}

			@Icon("2072")
			public static class Action implements Serializable
			{

				private static final long serialVersionUID = 1L;

				private String id;
				private String name;
				private String icon;
				private String description;

				public String getId()
				{
					return id;
				}

				public String getName()
				{
					return name;
				}

				public String getDescription()
				{
					return description;
				}

				public String getIcon()
				{
					return icon;
				}

				public JsonObject toJsonObject()
				{
					return new JsonObject()
						.setString("id", id)
						.setString("name", name)
						.setString("icon", icon)
						.setString("description", description);

				}

				@Override
				public String toString()
				{
					return toJsonObject().toString();
				}

				private static Action of(Method method)
				{
					var action = new Action();
					action.id = method.getName().substring(4);
					Name.Extractor.extract(method).ifPresent(e -> action.name = e);
					Description.Extractor.extract(method).ifPresent(e -> action.description = e);
					Icon.Extractor.extract(method).ifPresent(e -> action.icon = e.getCode());
					return action;
				}

				private static Action of(JsonObject jsonObject)
				{
					var action = new Action();
					jsonObject.getString("id").ifPresent(e -> action.id = e);
					jsonObject.getString("name").ifPresent(e -> action.name = e);
					jsonObject.getString("icon").ifPresent(e -> action.icon = e);
					jsonObject.getString("description").ifPresent(e -> action.description = e);
					return action;
				}
			}
		}
	}
}
