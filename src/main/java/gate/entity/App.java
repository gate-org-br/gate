package gate.entity;

import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.util.Icons;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
		Collections.sort(screens, (a, b) -> a.getPackage().getName().compareTo(b.getPackage().getName()));

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
				if (type.getSimpleName().endsWith("Screen"))
				{
					if (type.getSimpleName().length() > 6)
					{
						Module.Screen screen = module.addScreen();

						screen.id = type.getSimpleName().substring(0, type.getSimpleName().length() - 6);
						if (type.isAnnotationPresent(Name.class))
							screen.name = type.getAnnotation(Name.class).value();
						if (type.isAnnotationPresent(Description.class))
							screen.description = type.getAnnotation(Description.class).value();
						if (type.isAnnotationPresent(Icon.class))
							screen.icon = Icons.getInstance().get(type.getAnnotation(Icon.class).value()).getCode();

						for (Method method : type.getMethods())
						{
							if (method.getName().length() > 4
								&& method.getName().startsWith("call"))
							{
								Module.Screen.Action action = screen.addAction();
								action.id = method.getName().substring(4);
								if (method.isAnnotationPresent(Name.class))
									action.name = method.getAnnotation(Name.class).value();
								if (method.isAnnotationPresent(Description.class))
									action.description = method.getAnnotation(Description.class).value();
								if (method.isAnnotationPresent(Icon.class))
									action.icon = Icons.getInstance().get(method.getAnnotation(Icon.class).value()).getCode();
							}
						}
					} else
					{
						if (type.isAnnotationPresent(Name.class))
							module.name = type.getAnnotation(Name.class).value();
						if (type.isAnnotationPresent(Description.class))
							module.description = type.getAnnotation(Description.class).value();
						if (type.isAnnotationPresent(Icon.class))
							module.icon = Icons.getInstance().get(type.getAnnotation(Icon.class).value()).getCode();
					}
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
