package mock;

import gate.annotation.Entity;
import gate.sql.annotation.Column;
import gate.sql.annotation.Table;
import gate.type.ID;

@Entity
@Table("Contact")
public class ContactMock
{
	private int id;
	private Type type;
	private UserMock person;
	private String val;

	public enum Type
	{
		PHONE, EMAIL
	}

	public int getId()
	{
		return id;
	}

	public ContactMock setId(ID id)
	{
		this.id = id.getValue();
		return this;
	}

	public ContactMock setId(int id)
	{
		this.id = id;
		return this;
	}

	@Column("User$id")
	public UserMock getPerson()
	{
		return person == null ? person = new UserMock() : person;
	}

	public ContactMock setPerson(UserMock person)
	{
		this.person = person;
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
		return val;
	}

	public ContactMock setVal(String val)
	{
		this.val = val;
		return this;
	}
}
