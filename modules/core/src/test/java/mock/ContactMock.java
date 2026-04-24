package mock;

import gate.annotation.Converter;
import gate.converter.EnumStringConverter;
import gate.annotation.Entity;
import gate.type.ID;

@Entity
public class ContactMock
{

	private int id;
	private UserMock user;
	private Type type;
	private String value;

	public int getId()
	{
		return id;
	}

	public ContactMock setId(int id)
	{
		this.id = id;
		return this;
	}

	public ContactMock setId(ID id)
	{
		this.id = id.getValue();
		return this;
	}

	public UserMock getUser()
	{
		return user;
	}

	public ContactMock setUser(UserMock user)
	{
		this.user = user;
		return this;
	}

	public Type getType()
	{
		return type;
	}

	public ContactMock setType(Type type)
	{
		this.type = type;
		return this;
	}

	public String getVal()
	{
		return value;
	}

	public ContactMock setVal(String val)
	{
		this.value = val;
		return this;
	}

	public String getValue()
	{
		return value;
	}

	public ContactMock setValue(String value)
	{
		this.value = value;
		return this;
	}

	@Converter(EnumStringConverter.class)
	public enum Type
	{
		PHONE, EMAIL
	}
}
