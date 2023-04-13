package gate.entity;

import gate.annotation.Converter;
import gate.constraint.Min;
import gate.constraint.Required;
import gate.converter.EnumStringConverter;

public class Server
{

	@Required
	private Type type;

	@Required
	private String host;
	private String port;

	@Required
	private boolean useTLS;

	@Required
	private boolean useSSL;

	@Min(0)
	private Integer timeout;

	private String username;
	private String password;

	public Server()
	{
	}

	public Server(Type type)
	{
		this.type = type;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getPort()
	{
		return port;
	}

	public void setPort(String port)
	{
		this.port = port;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public Integer getTimeout()
	{
		return timeout;
	}

	public void setTimeout(Integer timeout)
	{
		this.timeout = timeout;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean getUseTLS()
	{
		return useTLS;
	}

	public void setUseTLS(boolean useTLS)
	{
		this.useTLS = useTLS;
	}

	public boolean getUseSSL()
	{
		return useSSL;
	}

	public void setUseSSL(boolean useSSL)
	{
		this.useSSL = useSSL;
	}

	@Converter(EnumStringConverter.class)
	public enum Type
	{
		SMTP
	}
}
