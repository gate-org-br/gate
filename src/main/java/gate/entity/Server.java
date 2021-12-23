package gate.entity;

import gate.annotation.Converter;
import gate.converter.EnumStringConverter;

public class Server
{

	Type type;
	String host;
	String port;
	String username;
	String password;

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

	public void setPassword(String password)
	{
		this.password = password;
	}

	@Converter(EnumStringConverter.class)
	public enum Type
	{
		SMTP
	}
}
