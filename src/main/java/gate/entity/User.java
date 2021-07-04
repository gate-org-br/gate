package gate.entity;

import gate.annotation.Description;
import gate.annotation.Entity;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Schema;
import gate.annotation.Table;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.constraint.Required;
import gate.type.CPF;
import gate.type.EMail;
import gate.type.ID;
import gate.type.Phone;
import gate.type.Sex;
import gate.type.mime.MimeData;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A User on the organization structure.
 * <p>
 * A User can have a Role and a list of authorizations
 */
@Entity
@Icon("2004")
@Table("Uzer")
@Schema("gate")
@Name("Usuário")
public class User implements Serializable
{

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
	private String userID;

	@Required
	@Maxlength(64)
	@Name("Senha do Usuário")
	@Description("O campo SENHA deve ser preenchido com no máximo 64 caracteres.")
	private String passwd;

	@Required
	@Maxlength(64)
	@Description("Os campos NOVA SENHA devem ser preenchidos com o mesmo texto de no máximo 64 caracteres.")
	private transient String change;

	@Required
	@Maxlength(64)
	@Description("Os campos NOVA SENHA devem ser preenchidos com o mesmo texto de no máximo 64 caracteres.")
	private transient String repeat;

	@Required
	@Maxlength(64)
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

	private Collection<Auth> auths;

	@Maxlength(1024)
	@Name("Comentários sobre o Usuário")
	@Description("O campo details deve ser preenchido com, no máximo, 1024 CARACTERES.")
	private String details;

	@Maxlength(24)
	@Name("Telefone Celular do Usuário")
	@Description("O campo Telefone Celular deve ser preenchido com, no máximo, 24 CARACTERES.")
	private Phone cellPhone;

	@Maxlength(24)
	@Name("Telefone Fixo do Usuário")
	@Description("O campo Telefone Fixo deve ser preenchido com, no máximo, 24 CARACTERES.")
	private Phone phone;

	@Name("Foto do Usuário")
	private MimeData photo;

	@Name("Data de Nascimento")
	private LocalDate birthdate;

	@Name("CPF")
	private CPF CPF;

	@Name("Sexo")
	private Sex sex;

	@Required
	@Name("Data de Cadastro")
	@Description("Data de cadastro do usuário.")
	private LocalDateTime registration;

	@Maxlength(32)
	@Name("Código do usuário")
	private String code;

	private List<Func> funcs;

	public Collection<Auth> getAuths()
	{
		if (auths == null)
			auths = new ArrayList<>();
		return auths;
	}

	public User setAuths(Collection<Auth> auths)
	{
		this.auths = auths;
		return this;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getRepeat()
	{
		return repeat;
	}

	public User setRepeat(String confirm)
	{
		this.repeat = confirm;
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

	public String getPasswd()
	{
		return passwd;
	}

	public User setPasswd(String passwd)
	{
		this.passwd = passwd;
		return this;
	}

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

	public String getUserID()
	{
		return userID;
	}

	public User setUserID(String userID)
	{
		this.userID = userID;
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

	public String getChange()
	{
		return change;
	}

	public void setChange(String change)
	{
		this.change = change;
	}

	@Override
	public String toString()
	{
		return name == null ? "Indefinido" : name;
	}

	public CPF getCPF()
	{
		return CPF;
	}

	public void setCPF(CPF CPF)
	{
		this.CPF = CPF;
	}

	public LocalDateTime getRegistration()
	{
		return registration;
	}

	public void setRegistration(LocalDateTime registration)
	{
		this.registration = registration;
	}

	public String getDetails()
	{
		return details;
	}

	public void setDetails(String details)
	{
		this.details = details;
	}

	public Phone getCellPhone()
	{
		return cellPhone;
	}

	public User setCellPhone(Phone cellPhone)
	{
		this.cellPhone = cellPhone;
		return this;
	}

	public Phone getPhone()
	{
		return phone;
	}

	public LocalDate getBirthdate()
	{
		return birthdate;
	}

	public void setBirthdate(LocalDate birthdate)
	{
		this.birthdate = birthdate;
	}

	public User setPhone(Phone phone)
	{
		this.phone = phone;
		return this;
	}

	public boolean isDisabled()
	{
		return Boolean.FALSE.equals(active) || getRole().isDisabled();
	}

	public MimeData getPhoto()
	{
		return photo;
	}

	public void setPhoto(MimeData photo)
	{
		this.photo = photo;
	}

	public Sex getSex()
	{
		return sex;
	}

	public void setSex(Sex sex)
	{
		this.sex = sex;
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

	public boolean checkBlock(String module, String screen, String action)
	{
		return computedAuthStream()
			.anyMatch(e -> e.blocks(module, screen, action));
	}

	public boolean checkAccess(String module, String screen, String action)
	{
		return computedAuthStream()
			.noneMatch(e -> e.blocks(module, screen, action))
			&& computedAuthStream()
				.anyMatch(e -> e.allows(module, screen, action));
	}
}
