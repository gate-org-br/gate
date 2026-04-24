package gate.entity;

import gate.type.ID;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable
{
	@Serial
	private static final long serialVersionUID = 1L;

	private ID id;
	private String name;

	public ID getId()
	{
		return id;
	}

	public User setId(ID id)
	{
		this.id = id;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public User setName(String name)
	{
		this.name = name;
		return this;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof User && Objects.equals(id, ((User) obj).id);
	}

	@Override
	public int hashCode()
	{
		return id == null ? 0 : id.getValue();
	}

	@Override
	public String toString()
	{
		return name == null ? "Indefinido" : name;
	}
}
