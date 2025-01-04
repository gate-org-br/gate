package gate.entity;

import gate.annotation.Description;
import gate.annotation.Entity;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.constraint.Maxlength;
import gate.constraint.Required;
import gate.type.ID;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Name("Func")
@Icon("2099")
public class Func implements Serializable
{

	@Required
	@Name("ID")
	@Description("ID da função")
	private ID id;

	@Required
	@Name("Nome")
	@Maxlength(64)
	@Description("Nome da função")
	private String name;

	private List<Auth> auths;
	private List<User> users;
	private List<Role> roles;

	public ID getId()
	{
		return id;
	}

	public void setId(ID id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<Auth> getAuths()
	{
		if (auths == null)
			auths = new ArrayList<>();
		return auths;
	}

	public void setAuths(List<Auth> auths)
	{
		this.auths = auths;
	}

	public List<User> getUsers()
	{
		if (users == null)
			users = new ArrayList<>();
		return users;
	}

	public void setUsers(List<User> users)
	{
		this.users = users;
	}

	public List<Role> getRoles()
	{
		if (roles == null)
			roles = new ArrayList<>();
		return roles;
	}

	public void setRoles(List<Role> roles)
	{
		this.roles = roles;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Func
			&& Objects.equals(id, ((Func) obj).id);
	}

	@Override
	public int hashCode()
	{
		return id != null
			? id.getValue() : 0;
	}

	public boolean allows(String module, String screen, String action)
	{
		return getAuths().stream().anyMatch(e -> e.granted(module, screen, action));
	}

	public boolean blocks(String module, String screen, String action)
	{
		return getAuths().stream().anyMatch(e -> e.blocked(module, screen, action));
	}

	@Override
	public String toString()
	{
		return name != null ? name : super.toString();
	}
}
