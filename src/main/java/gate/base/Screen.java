package gate.base;

import gate.code.PackageName;
import gate.converter.Converter;
import gate.error.AppException;
import gate.lang.property.CollectionAttribute;
import gate.lang.property.Property;
import gate.util.Page;
import gate.util.Paginator;
import gate.util.PropertyComparator;
import gate.util.Reflection;
import gate.util.ScreenServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.enterprise.inject.spi.Unmanaged;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public abstract class Screen extends Base
{

	private String orderBy;
	private Integer pageSize;
	private Integer pageIndx;
	private List<String> messages;
	private HttpServletRequest request;
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

	public void prepare(HttpServletRequest request,
		HttpServletResponse response)
		throws RuntimeException
	{
		this.request = request;
		this.response = response;

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
							value = converter.ofPart(property.getRawType(), part);
							part.delete();
						} else if (value instanceof String)
							value = converter.ofString(property.getRawType(), (String) value);
						property.setValue(this, value);
					}
				} catch (IOException ex)
				{
					getMessages().add(ex.getMessage());
				}
			}
		});
	}

	public Object execute(Method method) throws Throwable
	{
		try
		{
			return method.invoke(this);
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
		return (ScreenServletRequest) request;
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
				.map(e -> e.asSubclass(Screen.class));
		} catch (ClassNotFoundException ex)
		{
			return Optional.empty();
		}
	}

	public static Optional<Method> getAction(Class<Screen> clazz, String action)
	{
		return Reflection.findMethod(clazz, action != null ? "call" + action : "call");
	}

	public static Optional<Method> getAction(String module, String screen, String action)
	{
		return getScreen(module, screen).flatMap(e -> getAction(e, action));
	}

	public String getDefaultJSP(Method method)
	{
		String screenName = getClass().getSimpleName();
		PackageName packageName = PackageName.of(getClass());
		return "/WEB-INF/vies/"
			+ packageName.getFolderName()
			+ "/"
			+ screenName.substring(0, screenName.length() - 6)
			+ "/"
			+ method.getName().substring(4);
	}
}
