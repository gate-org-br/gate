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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

	public boolean isMasterOf(Role role)
	{
		return equals(role.getMasterRole());
	}

	public boolean isDetailOf(Role role)
	{
		return role.equals(getMasterRole());
	}

	public Role getMasterRole()
	{
		return parents().filter(e -> Boolean.TRUE.equals(e.master))
				.findFirst()
				.orElse(this);
	}

	public boolean isDisabled()
	{
		return parents().anyMatch(e -> Boolean.FALSE.equals(active));
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

	private List<Auth> getPrivateAuths()
	{
		return id != null ? getAuths().stream()
				.filter(e -> Auth.Type.PRIVATE.equals(e.getType()))
				.collect(Collectors.toList()) : Collections.emptyList();
	}

	private List<Auth> getPublicAuths()
	{
		return id != null
				? Stream.concat(getAuths().stream()
						.filter(e -> Auth.Type.PUBLIC.equals(e.getType())),
						getRole().getPublicAuths().stream())
						.collect(Collectors.toList()) : Collections.emptyList();
	}

	List<Auth> getAllAuths()
	{
		return id != null ? Stream.concat(getPrivateAuths().stream(),
				getPublicAuths().stream()).collect(Collectors.toList())
				: Collections.emptyList();
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
