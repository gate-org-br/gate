package gate.entity;

import gate.annotation.*;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.constraint.Required;
import gate.sql.annotation.Schema;
import gate.sql.annotation.Table;
import gate.type.EMail;
import gate.type.ID;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Users on the organization structure.
 * <p>
 * A Users can have a Role and a list valueOf authorizations
 */
@Name
@Entity
@Icon("2004")
@Table("Uzer")
@Schema("gate")
public class User implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	@Required
	@Description
	private ID id;

	@Required
	@Name
	@Description
	private Boolean active;

	@Required
	@Maxlength(64)
	@Name
	@Description
	private String username;

	@Required
	@Maxlength(64)
	@Name
	@Description
	private String password;

	@Required
	@Maxlength(128)
	@Name
	@Description
	private String name;

	@Maxlength(64)
	@Pattern(EMail.REGEX)
	@Name
	@Description
	private String email;

	@Description
	private Role role;

	@Name
	@Required
	@Description
	private LocalDateTime creation;

	private List<Auth> auths;

	private List<Func> funcs;

	@NullSafe
	public List<Auth> getAuths()
	{
		if (auths == null)
			auths = new ArrayList<>();
		return auths;
	}

	public User setAuths(List<Auth> auths)
	{
		this.auths = auths;
		return this;
	}

	public ID getId()
	{
		return id;
	}

	public User setId(ID id)
	{
		this.id = id;
		return this;
	}

	public Boolean getActive()
	{
		return active;
	}

	public User setActive(Boolean active)
	{
		this.active = active;
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

	public String getPassword()
	{
		return password;
	}

	public User setPassword(String password)
	{
		this.password = password;
		return this;
	}

	@NullSafe
	public Role getRole()
	{
		if (role == null)
			role = new Role();
		return role;
	}

	public User setRole(Role role)
	{
		this.role = role;
		return this;
	}

	public String getUsername()
	{
		return username;
	}

	public User setUsername(String username)
	{
		this.username = username;
		return this;
	}

	public String getEmail()
	{
		return email;
	}

	public User setEmail(String email)
	{
		this.email = email;
		return this;
	}

	@NullSafe
	public List<Func> getFuncs()
	{
		if (funcs == null)
			funcs = new ArrayList<>();
		return funcs;
	}

	public User setFuncs(List<Func> funcs)
	{
		this.funcs = funcs;
		return this;
	}

	public LocalDateTime getCreation() {return creation;}

	public User setCreation(LocalDateTime creation)
	{
		this.creation = creation;
		return this;
	}

	@Override
	public boolean equals(Object obj)
	{

		return obj instanceof User
		       && Objects.equals(this.getId(), ((User) obj).getId());
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

	public boolean isDisabled()
	{
		return Boolean.FALSE.equals(active) || getRole().isDisabled();
	}

	public Stream<Auth> computedAuthStream()
	{
		return id != null ? Stream.concat(getAuths().stream(),
				Stream.concat(getFuncs().stream().flatMap(e -> e.getAuths().stream()),
						getRole().computedAuthStream())) : Stream.empty();
	}

	public List<Auth> getComputedAuths()
	{
		return computedAuthStream().collect(Collectors.toList());
	}

	public boolean isSuperUser()
	{
		return computedAuthStream().anyMatch(Auth::isSuperAuth);
	}

	public boolean checkAccess(String module, String screen, String action)
	{
		return computedAuthStream()
					   .noneMatch(e -> e.blocked(module, screen, action))
		       && computedAuthStream()
					   .anyMatch(e -> e.granted(module, screen, action));
	}

	public boolean checkSpecificAccess(String module, String screen, String action)
	{
		return computedAuthStream()
					   .noneMatch(e -> e.blocked(module, screen, action))
		       && computedAuthStream()
					   .anyMatch(e -> e.equals(module, screen, action));
	}

	public User unwrap()
	{
		return this;
	}

	public static User valueOf(String string)
	{
		return new User().setId(ID.valueOf(string));
	}
}