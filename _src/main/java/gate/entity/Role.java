package gate.entity;

import gate.annotation.Column;
import gate.annotation.Description;
import gate.annotation.Entity;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Schema;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.constraint.Required;
import gate.type.Hierarchy;
import gate.type.ID;
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
 * A Role can contain users, other roles and a list of authorizations
 */
@Entity
@Icon("2005")
@Schema("gate")
public class Role implements Serializable, Hierarchy<Role>
{

	private static final long serialVersionUID = 1L;

	@Required
	@Name("Perfil")
	@Description("O campo Perfil é requerido.")
	private ID id;

	@Required
	@Name("Active")
	@Description("Determina se o perfil está ativo. Usuários de perfis inativos não podem fazer logon")
	private Boolean active;

	@Name("Supergrupo")
	private Role role;

	@Name("Gerente")
	@Column("Manager")
	@Description("Entre com o gerente do grupo.")
	private User manager;

	@Required
	@Name("Nome")
	@Maxlength(64)
	@Description("O campo NOME é requerido e deve ser preenchido com, no máximo, 64 CARACTERES.")
	private String name;

	@Maxlength(256)
	@Name("Descrição")
	@Description("O campo DESCRIÇÃO deve ser preenchido com, no máximo, 256 CARACTERES.")
	private String description;

	@Required
	@Name("Master")
	@Description("Define se o perfil é Master.")
	private Boolean master;

	@Name("Sigla")
	@Maxlength(16)
	@Description("Entre com a sigla do grupo.")
	private String roleID;

	@Maxlength(64)
	@Name("E-Mail")
	@Pattern("^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$")
	@Description("O campo EMail deve ser preenchido com um email válido com no máximo 64 caracteres.")
	private String email;

	@Name("Busca Hierárquica")
	@Description("Deseja que a pesquisa seja feita de forma hierárquica?")
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

	public String getRoleID()
	{
		return roleID;
	}

	public Role setRoleID(String roleID)
	{
		this.roleID = roleID;
		return this;
	}

	public User getManager()
	{
		if (manager == null)
			manager = new User();
		return manager;
	}

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
			.filter(e -> Auth.Type.PRIVATE.equals(e.getType())),
			getFuncs().stream().flatMap(e -> e.getAuths().stream())
				.filter(e -> Auth.Type.PRIVATE.equals(e.getType())))
			: Stream.empty();
	}

	private Stream<Auth> publicAuthStream()
	{

		return id != null
			? Stream.concat(getAuths().stream()
				.filter(e -> Auth.Type.PUBLIC.equals(e.getType())),
				Stream.concat(getFuncs().stream().flatMap(e -> e.getAuths().stream())
					.filter(e -> Auth.Type.PUBLIC.equals(e.getType())),
					getRole().publicAuthStream())) : Stream.empty();
	}

	public Stream<Auth> computedAuthStream()
	{
		return id != null ? Stream.concat(privateAuthStream(),
			publicAuthStream()) : Stream.empty();
	}

	public List<Auth> getComputedAuths()
	{
		return computedAuthStream().collect(Collectors.toList());
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
}
