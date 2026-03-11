package gate.entity;

import gate.annotation.*;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.constraint.Required;
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
 * A User on the organization structure.
 * <p>
 * A User can have a Role and a list valueOf authorizations
 */
@Entity
@Icon("2004")
@Table("Uzer")
@Schema("gate")
@Name("Usuário")
public class User implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	@Required
	@Description("O campo USUÁRIO é requerido.")
	private ID id;

	@Required
	@Name("Ativo")
	@Description("O campo ATIVO é requerido.")
	private Boolean active;

	@Required
	@Maxlength(64)
	@Name("Login do Usuário")
	@Description("O campo LOGIN deve possuir no máximo 64 caracteres.")
	private String username;

	@Required
	@Maxlength(64)
	@Name("Senha do Usuário")
	@Description("O campo SENHA deve ser preenchido com no máximo 64 caracteres.")
	private String password;

	@Required
	@Maxlength(128)
	@Name("Nome do Usuário")
	@Description("O campo NOME deve ser preenchido com, no máximo, 64 CARACTERES.")
	private String name;

	@Maxlength(64)
	@Pattern(EMail.REGEX)
	@Name("E-Mail do Usuário")
	@Description("O campo EMail deve ser preenchido com um email válido com no máximo 64 caracteres.")
	private String email;

	@Description("Define o perfil do usuário.")
	private Role role;

	@Required
	@Name("Data de Cadastro")
	@Description("Data de cadastro do usuário.")
	private LocalDateTime creation;

	@Required
	@Name("Data de Cadastro")
	@Description("Data de cadastro do usuário.")
	private LocalDateTime activity;

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

	public LocalDateTime getCreation()
	{
		return creation;
	}

	public void setCreation(LocalDateTime creation)
	{
		this.creation = creation;
	}

	public LocalDateTime getActivity()
	{
		return activity;
	}

	public void setActivity(LocalDateTime activity)
	{
		this.activity = activity;
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