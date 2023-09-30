package gate;

import gate.annotation.Converter;
import gate.annotation.Entity;
import gate.converter.EnumStringConverter;
import gate.type.ID;

@Entity
public class Contact
{

	private ID id;
	private Person person;
	private Type type;
	private String val;

	public ID getId()
	{
		return id;
	}

	public Contact setId(ID id)
	{
		this.id = id;
		return this;
	}

	public Person getPerson()
	{
		if (person == null)
			person = new Person();
		return person;
	}

	public Contact setPerson(Person person)
	{
		this.person = person;
		return this;
	}

	public Type getType()
	{
		return type;
	}

	public Contact setType(Type type)
	{
		this.type = type;
		return this;
	}

	public String getVal()
	{
		return val;
	}

	public Contact setVal(String val)
	{
		this.val = val;
		return this;
	}

	@Converter(EnumStringConverter.class)
	public enum Type
	{
		PHONE, EMAIL
	}
}
