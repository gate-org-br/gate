package gate.entity;

import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.ServletContext;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

@Icon("2189")
public class App implements Serializable
{

	private App()
	{
	}

	private String id;
	private String name;
	private String description;
	private static final long serialVersionUID = 1L;
	private final List<Module> modules = new CopyOnWriteArrayList<>();

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
		return Collections.unmodifiableList(modules);
	}

	public static App getInstance(ServletContext context)
	{
		Reflections reflections = new Reflections(ClasspathHelper.forWebInfClasses(context));

		List<Class<? extends gate.base.Screen>> screens
			= new ArrayList<>(reflections.getSubTypesOf(gate.base.Screen.class));
		screens.removeIf(e -> Modifier.isAbstract(e.getModifiers()) || !e.getSimpleName().endsWith("Screen"));
		screens.sort(Comparator.comparing(a -> a.getPackage().getName()));

		App app = new App();
		app.id = context.getServletContextName();
		app.name = context.getInitParameter("name");
		app.description = context.getInitParameter("description");

		Iterator<Class<? extends gate.base.Screen>> iterator = screens.iterator();
		while (iterator.hasNext())
		{
			Class<? extends gate.base.Screen> type = iterator.next();

			Module module = app.addModule();
			module.id = type.getPackage().getName();

			do
			{
				if (type.getSimpleName().length() > 6)
				{
					Module.Screen screen = module.addScreen();

					screen.id = type.getSimpleName().substring(0, type.getSimpleName().length() - 6);
					Name.Extractor.extract(type).ifPresent(e -> screen.name = e);
					Description.Extractor.extract(type).ifPresent(e -> screen.description = e);
					Icon.Extractor.extract(type).ifPresent(e -> screen.icon = e.getCode());

					for (Method method : type.getMethods())
					{
						if (method.getName().length() > 4
							&& method.getName().startsWith("call"))
						{
							Module.Screen.Action action = screen.addAction();
							action.id = method.getName().substring(4);

							Name.Extractor.extract(method).ifPresent(e -> action.name = e);
							Description.Extractor.extract(method).ifPresent(e -> action.description = e);
							Icon.Extractor.extract(method).ifPresent(e -> action.icon = e.getCode());
						}
					}
				} else
				{
					Name.Extractor.extract(type).ifPresent(e -> module.name = e);
					Description.Extractor.extract(type).ifPresent(e -> module.description = e);
					Icon.Extractor.extract(type).ifPresent(e -> module.icon = e.getCode());
				}

				if (!iterator.hasNext())
					break;
				type = iterator.next();
			} while (type.getPackage().getName().equals(module.getId()));
		}

		return app;
	}

	public Module addModule()
	{
		Module module = new Module();
		modules.add(module);
		return module;
	}

	@Icon("2006")
	public final class Module implements Serializable
	{

		private static final long serialVersionUID = 1L;

		private String id;
		private String icon;
		private String name;
		private String description;
		private final List<Screen> screens = new ArrayList<>();

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
			return Collections.unmodifiableList(screens);
		}

		public App getApp()
		{
			return App.this;
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

		@Icon("2044")
		public class Screen implements Serializable
		{

			private static final long serialVersionUID = 1L;

			private String id;
			private String name;
			private String icon;
			private String description;
			private final List<Action> actions = new ArrayList<>();

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
				return Collections.unmodifiableList(actions);
			}

			public String getDescription()
			{
				return description;
			}

			public Module getModule()
			{
				return Module.this;
			}

			public String getIcon()
			{
				return icon;
			}

			public Action addAction()
			{
				Action action = new Action();
				actions.add(action);
				return action;
			}

			@Icon("2072")
			public class Action implements Serializable
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
			}
		}
	}
}
