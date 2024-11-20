package gate.base;

import gate.annotation.BodyParameter;
import gate.annotation.PathParameter;
import gate.annotation.QueryParameter;
import gate.converter.Converter;
import gate.error.AppException;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.error.NoSuchPropertyError;
import gate.error.UncheckedConversionException;
import gate.http.ScreenServletRequest;
import gate.lang.property.CollectionAttribute;
import gate.lang.property.Property;
import gate.util.Page;
import gate.util.Paginator;
import gate.util.PropertyComparator;
import gate.util.Reflection;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.enterprise.inject.spi.Unmanaged;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public abstract class Screen extends Base
{

	private String orderBy;
	private Integer pageSize;
	private Integer pageIndx;
	private List<String> messages;
	private ScreenServletRequest request;
	private HttpServletResponse response;

	public static Screen create(Class<Screen> clazz)
	{
		Unmanaged.UnmanagedInstance<Screen> instance = null;
		try
		{
			instance = new Unmanaged<>(clazz).newInstance();
			return instance.produce().inject().postConstruct().get();
		} finally
		{
			if (instance != null)
				instance.preDestroy().dispose();
		}
	}

	public void prepare(ScreenServletRequest request, HttpServletResponse response) throws BadRequestException
	{
		this.request = request;
		this.response = response;

		try
		{
			getRequest().getParameterList().stream().sorted().forEach(name ->
			{
				Property property = Property.parse(getClass(), name);
				if (property != null)
				{
					try
					{
						if (property.getLastAttribute() instanceof CollectionAttribute)
						{
							Property previous = property.getPreviousProperty();
							previous.setValue(this, getRequest()
								.getParameterValues(previous.getRawType(), property.getRawType(), name));
						} else
						{
							Converter converter = property.getConverter();
							Object value = getRequest().getParameterValue(name);
							if (value instanceof Part)
							{
								Part part = (Part) value;
								try
								{
									value = converter.ofPart(property.getRawType(), part);
								} finally
								{
									try
									{
										part.delete();
									} catch (IOException ex)
									{
										throw new UncheckedIOException(ex);
									}
								}
							} else if (value instanceof String)
								value = converter.ofString(property.getRawType(), (String) value);
							property.setValue(this, value);
						}
					} catch (ConversionException ex)
					{
						throw new UncheckedConversionException(ex);
					}
				}
			});
		} catch (UncheckedConversionException | NoSuchPropertyError ex)
		{
			throw new BadRequestException(ex.getCause().getMessage());
		}
	}

	public Object execute(Method method) throws Throwable
	{
		var parameters = new ArrayList<>();
		for (var parameter : method.getParameters())
		{
			Object value;

			if (parameter.isAnnotationPresent(PathParameter.class))
				value = PathParameter.Extractor.extract(getRequest(), parameter);
			else if (parameter.isAnnotationPresent(BodyParameter.class))
				value = BodyParameter.Extractor.extract(getRequest(), parameter);
			else
				value = QueryParameter.Extractor.extract(getRequest(), parameter);

			parameters.add(value);
		}

		try
		{
			return method.invoke(this, parameters.toArray());
		} catch (InvocationTargetException ex)
		{
			throw ex.getCause();
		}
	}

	public Integer getDefaultPageSize()
	{
		return 10;
	}

	public String getModule()
	{
		return getRequest().getParameter("MODULE");
	}

	public String getScreen()
	{
		return getRequest().getParameter("SCREEN");
	}

	public String getAction()
	{
		return getRequest().getParameter("ACTION");
	}

	public ScreenServletRequest getRequest()
	{
		return request;
	}

	public HttpServletResponse getResponse()
	{
		return response;
	}

	public boolean isGET()
	{
		return "GET".equals(request.getMethod());
	}

	public boolean isDELETE()
	{
		return "DELETE".equals(request.getMethod());
	}

	public boolean isPOST()
	{
		return "POST".equals(request.getMethod());
	}

	public boolean isPUT()
	{
		return "PUT".equals(request.getMethod());
	}

	public boolean isPATCH()
	{
		return "PATCH".equals(request.getMethod());
	}

	public String getMethod()
	{
		return request.getMethod().toLowerCase();
	}

	public List<String> getMessages()
	{
		if (messages == null)
			messages = new LinkedList<>();
		return messages;
	}

	public void setMessages(Exception ex)
	{
		setMessages(ex.getMessage());
	}

	public void setMessages(AppException ex)
	{
		setMessages(ex.getMessages());
	}

	public void setMessages(String... messages)
	{
		this.messages = List.of(messages);
	}

	public void setMessages(List<String> messages)
	{
		this.messages = messages;
	}

	public Integer getPageIndx()
	{
		if (pageIndx == null)
			pageIndx = 0;
		return pageIndx;
	}

	public void setPageIndx(Integer pageIndx)
	{
		this.pageIndx = pageIndx;
	}

	public Integer getPageSize()
	{
		if (pageSize == null)
			pageSize = getDefaultPageSize();
		return pageSize;
	}

	public void setPageSize(Integer pageSize)
	{
		this.pageSize = pageSize;
	}

	public String getOrderBy()
	{
		return orderBy;
	}

	public void setOrderBy(String orderBy)
	{
		this.orderBy = orderBy;
	}

	public <R> Page<R> paginate(List<R> data)
	{
		return new Paginator<>(data, getPageSize()).getPage(getPageIndx());
	}

	public <T> List<T> ordenate(List<T> data)
	{
		if (getOrderBy() != null)
			data.sort(new PropertyComparator(getOrderBy()));
		return data;
	}

	public static Optional<Class<Screen>> getScreen(String module, String screen)
	{
		try
		{
			return Optional.of(Thread.currentThread().getContextClassLoader().loadClass(screen != null
				? module + "." + screen
				+ "Screen" : module + ".Screen"))
				.map(e -> e.asSubclass(Screen.class
			));
		} catch (ClassNotFoundException ex)
		{
			return Optional.empty();
		}
	}

	public static Optional<Method> getAction(Class<Screen> clazz, String action)
	{
		return Reflection.findMethodByName(clazz, action != null ? "call" + action : "call");
	}

	public static Optional<Method> getAction(String module, String screen, String action)
	{
		return getScreen(module, screen).flatMap(e -> getAction(e, action));
	}
}
