package gate.handler;

import com.itextpdf.text.log.LoggerFactory;
import gate.error.AppError;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Handlers
{

	static final Handlers INSTANCE = new Handlers();

	private final Map<Class<? extends Handler>, Handler> HANDLERS = new ConcurrentHashMap<>();
	private final Map<Class<?>, Handler> INSTANCES = new ConcurrentHashMap<>();

	static
	{
		Locale.setDefault(new Locale("pt", "br"));
	}

	private Handlers()
	{
		HANDLERS.put(DocHandler.class, new DocHandler());
		HANDLERS.put(HttpErrorHandler.class, new HttpErrorHandler());
		HANDLERS.put(FileHandler.class, new FileHandler());
		HANDLERS.put(IntegerHandler.class, new IntegerHandler());
		HANDLERS.put(JsonElementHandler.class, new JsonElementHandler());
		HANDLERS.put(MimeDataFileHandler.class, new MimeDataFileHandler());
		HANDLERS.put(MimeTextHandler.class, new MimeTextHandler());
		HANDLERS.put(JsonHandler.class, new JsonHandler());
		HANDLERS.put(OptionHandler.class, new OptionHandler());
		HANDLERS.put(PNGHandler.class, new PNGHandler());
		HANDLERS.put(ReportHandler.class, new ReportHandler());
		HANDLERS.put(TextHandler.class, new TextHandler());
		HANDLERS.put(URLHandler.class, new URLHandler());
		HANDLERS.put(StringHandler.class, new StringHandler());
		HANDLERS.put(EnumHandler.class, new EnumHandler());

		INSTANCES.put(String.class, new StringHandler());
		INSTANCES.put(Object.class, new SerializableHandler());
		INSTANCES.put(File.class, new FileHandler());
		INSTANCES.put(Integer.class, new IntegerHandler());
		INSTANCES.put(Enum.class, new EnumHandler());
	}

	public Handler get(Class<?> type)
	{
		return INSTANCES.computeIfAbsent(type, e ->
		{
			try
			{
				for (Class<?> clazz = e;
					clazz != null;
					clazz = clazz.getSuperclass())
					if (INSTANCES.containsKey(clazz))
						return INSTANCES.get(clazz);
					else if (clazz.isAnnotationPresent(gate.annotation.Handler.class))
						return clazz.getAnnotation(gate.annotation.Handler.class)
							.value().getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex)
			{
				LoggerFactory.getLogger(Handlers.class).error(ex.getMessage(), ex);
			}

			return INSTANCES.get(Object.class);
		});
	}

	public Handler getInstance(Class<? extends Handler> type)
	{
		return HANDLERS.computeIfAbsent(type, e ->
		{
			try
			{
				return e.getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException
				| NoSuchMethodException | InvocationTargetException ex)
			{
				throw new AppError(ex);
			}
		});
	}

	@Override
	public String toString()
	{
		return INSTANCES.toString();
	}
}
