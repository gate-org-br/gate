package mock;

import gate.annotation.NullSafe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoleMock
{
	private IDMock id;
	private String name;
	private Boolean active;
	private RoleMock role;
	private UserMock manager;
	private List<UserMock> users;

	public IDMock getId() {return id;}

	public RoleMock setId(IDMock id)
	{
		this.id = id;
		return this;
	}

	public String getName() {return name;}

	public RoleMock setName(String name)
	{
		this.name = name;
		return this;
	}

	public Boolean getActive()
	{
		return active;
	}

	public RoleMock setActive(Boolean active)
	{
		this.active = active;
		return this;
	}

	@NullSafe
	public RoleMock getRole() {return role == null ? role = new RoleMock() : role;}

	public RoleMock setRole(RoleMock role)
	{
		this.role = role;
		return this;
	}

	public UserMock getManager()
	{
		return manager == null ? manager = new UserMock() : manager;
	}

	public RoleMock setManager(UserMock manager)
	{
		this.manager = manager;
		return this;
	}

	public List<UserMock> getUsers()
	{
		return users == null ? users = new ArrayList<>() : users;
	}

	public RoleMock setUsers(List<UserMock> users)
	{
		this.users = users;
		return this;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof RoleMock role
				&& Objects.equals(id, role.id)
				&& Objects.equals(name, role.name)
				&& Objects.equals(active, role.active);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, name, active);
	}
}
