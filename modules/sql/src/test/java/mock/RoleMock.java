package mock;

import gate.annotation.Entity;
import gate.sql.annotation.Table;
import gate.type.ID;

@Entity
@Table("Role")
public class RoleMock
{
	private ID id;
	private String name;

	public ID getId()
	{
		return id;
	}

	public RoleMock setId(ID id)
	{
		this.id = id;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public RoleMock setName(String name)
	{
		this.name = name;
		return this;
	}
}
