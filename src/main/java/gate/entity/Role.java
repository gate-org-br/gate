package gate.entity;

import gate.annotation.*;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.constraint.Required;
import gate.type.Hierarchy;
import gate.type.ID;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Role on the organization structure.
 * <p>
 * A Role can contain users, other roles and a list valueOf authorizations
 */
@Entity
@Icon("2005")
@Schema("gate")
public class Role implements Serializable, Hierarchy<Role>
{

	@Serial private static final long serialVersionUID = 1L;

	@Required
	@Name
	@Description
	private ID id;

	@Required
	@Name
	@Description
	private Boolean active;

	@Name
	private Role role;

	@Name
	@Column("Manager")
	@Description
	private User manager;

	@Required
	@Name
	@Maxlength(64)
	@Description
	private String name;

	@Maxlength(256)
	@Name
	@Description
	private String description;

	@Required
	@Name
	@Description
	private Boolean master;

	@Name
	@Maxlength(16)
	@Description
	private String rolename;

	@Maxlength(64)
	@Name
	@Pattern("^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$")
	@Description
	private String email;

	@Name
	@Description
	private Boolean recursive;

	private List<Auth> auths;

	private List<User> users;

	private List<Role> roles;

	private List<Func> funcs;

	@Override
	public ID getId()
	{
		return id;
	}

	@Override
	public List<Role> getChildren()
	{
		return getRoles();
	}

	@Override
	public Role getParent()
	{
		return getRole();
	}

	@Override
	public Role setChildren(List<Role> children)
	{
		return setRoles(children);
	}

	@Override
	public Role setParent(Role parent)
	{
		return setRole(parent);
	}

	public Role setId(ID id)
	{
		this.id = id;
		return this;
	}

	@NullSafe
	public Role getRole()
	{
		if (role == null)
			role = new Role();
		return role;
	}

	public Role setRole(Role role)
	{
		this.role = role;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Role setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getEmail()
	{
		return email;
	}

	public Role setEmail(String email)
	{
		this.email = email;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public Role setDescription(String description)
	{
		this.description = description;
		return this;
	}

	@NullSafe
	public List<User> getUsers()
	{
		if (users == null)
			users = new ArrayList<>();
		return users;
	}

	public Role setUsers(List<User> users)
	{
		this.users = users;
		return this;
	}

	@NullSafe
	public List<Func> getFuncs()
	{
		if (funcs == null)
			funcs = new ArrayList<>();
		return funcs;
	}

	public void setFuncs(List<Func> funcs)
	{
		this.funcs = funcs;
	}

	@NullSafe
	public List<Role> getRoles()
	{
		if (roles == null)
			roles = new ArrayList<>();
		return roles;
	}

	public Role setRoles(List<Role> roles)
	{
		this.roles = roles;
		return this;
	}

	@NullSafe
	public List<Auth> getAuths()
	{
		if (auths == null)
			auths = new ArrayList<>();
		return auths;
	}

	public Role setAuths(List<Auth> auth)
	{
		this.auths = auth;
		return this;
	}

	public boolean isDisabled()
	{
		return parentStream()
				.anyMatch(e -> Boolean.FALSE.equals(active));
	}

	public String getRolename()
	{
		return rolename;
	}

	public Role setRolename(String rolename)
	{
		this.rolename = rolename;
		return this;
	}

	@NullSafe
	public User getManager() {return manager == null ? new User() : manager;}

	public Role setManager(User manager)
	{
		this.manager = manager;
		return this;
	}

	public Boolean getRecursive()
	{
		return recursive;
	}

	public void setRecursive(Boolean recursive)
	{
		this.recursive = recursive;
	}

	public Boolean getActive()
	{
		return active;
	}

	public Role setActive(Boolean active)
	{
		this.active = active;
		return this;
	}

	public Boolean getMaster()
	{
		return master;
	}

	public Boolean isMaster()
	{
		return master;
	}

	public Role setMaster(Boolean master)
	{
		this.master = master;
		return this;
	}

	public Role getMasterRole()
	{
		return parentStream()
				.filter(e -> Boolean.TRUE.equals(e.master))
				.findFirst()
				.orElse(this);
	}

	public boolean isMasterOf(Role role)
	{
		return slaveStream().anyMatch(e -> e.equals(role));
	}

	public boolean isSlaveOf(Role role)
	{
		return getMasterRole().equals(role);
	}

	public Stream<Role> slaveStream()
	{
		return Boolean.TRUE.equals(getMaster())
				? Stream.concat(Stream.of(this), getChildren().stream()
												 .filter(e -> !Boolean.TRUE.equals(e.getMaster()))
												 .flatMap(Hierarchy::stream))
				: Stream.empty();
	}

	public List<Role> toSlaveList()
	{
		return slaveStream().collect(Collectors.toList());
	}

	public <T> List<T> toSlaveList(Function<Role, T> extractor)
	{
		return slaveStream().map(extractor).collect(Collectors.toList());
	}

	public Stream<Role> masterStream()
	{
		return stream()
				.filter(e -> Boolean.TRUE.equals(e.getMaster()));
	}

	public List<Role> toMasterList()
	{
		return masterStream().collect(Collectors.toList());
	}

	public <T> List<T> toMasterList(Function<Role, T> extractor)
	{
		return masterStream().map(extractor).collect(Collectors.toList());
	}

	private Stream<Auth> privateAuthStream()
	{
		return id != null ? Stream.concat(getAuths().stream()
										  .filter(e -> Auth.Scope.PRIVATE.equals(e.getScope())),
				getFuncs().stream().flatMap(e -> e.getAuths().stream())
				.filter(e -> Auth.Scope.PRIVATE.equals(e.getScope())))
				: Stream.empty();
	}

	private Stream<Auth> publicAuthStream()
	{

		return id != null
				? Stream.concat(getAuths().stream()
								.filter(e -> Auth.Scope.PUBLIC.equals(e.getScope())),
				Stream.concat(getFuncs().stream().flatMap(e -> e.getAuths().stream())
							  .filter(e -> Auth.Scope.PUBLIC.equals(e.getScope())),
						getRole().publicAuthStream())) : Stream.empty();
	}

	Stream<Auth> computedAuthStream()
	{
		return id != null ? Stream.concat(privateAuthStream(),
				publicAuthStream()) : Stream.empty();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Role && Objects.equals(id, ((Role) obj).id);
	}

	@Override
	public int hashCode()
	{
		return id != null ? id.getValue() : 0;
	}

	@Override
	public String toString()
	{
		return getRole().getId() != null
				? String.format("%s / %s", getRole().toString(), getName())
				: getName();
	}

	public static Role valueOf(String string)
	{
		return new Role().setId(ID.valueOf(string));
	}
}