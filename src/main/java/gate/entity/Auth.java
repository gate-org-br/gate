package gate.entity;

import gate.annotation.Color;
import gate.annotation.Column;
import gate.annotation.Converter;
import gate.annotation.Description;
import gate.annotation.Entity;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Schema;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.constraint.Required;
import gate.converter.EnumStringConverter;
import gate.type.ID;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Icon("2002")
@Schema("gate")
public class Auth implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Required()
	@Description("O campo ID é requerido.")
	private ID id;

	private Role role;

	@Column("Uzer")
	private User user;

	private Func func;

	@Required
	@Description("Defina o tipo do acesso. Acessos públicos são herdados e acessos privados não.")
	private Scope scope;

	@Required
	@Description("Defina o modo do acesso.")
	private Access access;

	@Maxlength(64)
	@Pattern("^[$a-zA-Z0-9_.]*$")
	@Description("O campo MÓDULO deve ser preenchido com, NO máximo, 64 LETRAS.")
	private String module;

	@Maxlength(64)
	@Pattern("^[$a-zA-Z0-9_]*$")
	@Description("O campo SCREEN deve ser preenchido com, NO máximo, 64 LETRAS.")
	private String screen;

	@Maxlength(64)
	@Pattern("^[$a-zA-Z0-9_]*$")
	@Description("O campo ACTION deve ser preenchido com, NO máximo, 64 LETRAS.")
	private String action;

	public User getUser()
	{
		if (user == null)
			user = new User();
		return user;
	}

	public Auth setUser(User user)
	{
		this.user = user;
		return this;
	}

	public String getAction()
	{
		return action;
	}

	public Auth setAction(String action)
	{
		this.action = action;
		return this;
	}

	public ID getId()
	{
		return id;
	}

	public Auth setId(ID id)
	{
		this.id = id;
		return this;
	}

	public String getModule()
	{
		return module;
	}

	public Auth setModule(String module)
	{
		this.module = module;
		return this;
	}

	public Role getRole()
	{
		if (role == null)
			role = new Role();
		return role;
	}

	public Auth setRole(Role role)
	{
		this.role = role;
		return this;
	}

	public Func getFunc()
	{
		if (func == null)
			func = new Func();
		return func;
	}

	public Auth setFunc(Func func)
	{
		this.func = func;
		return this;
	}

	public String getScreen()
	{
		return screen;
	}

	public Auth setScreen(String screen)
	{
		this.screen = screen;
		return this;
	}

	public Scope getScope()
	{
		return scope;
	}

	public Auth setScope(Scope scope)
	{
		this.scope = scope;
		return this;
	}

	public Access getAccess()
	{
		return access;
	}

	public Auth setAccess(Access access)
	{
		this.access = access;
		return this;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Auth && Objects
			.equals(id, ((Auth) obj).id);
	}

	@Override
	public int hashCode()
	{
		return id != null ? id.getValue() : 0;
	}

	@Override
	public String toString()
	{
		return new StringJoiner(", ")
			.add("MODULE: " + (module != null ? module : "*"))
			.add("SCREEN: " + (screen != null ? screen : "*"))
			.add("ACTION: " + (action != null ? action : "*"))
			.toString();
	}

	public boolean isSuperAuth()
	{
		return Access.GRANT == access
			&& module == null
			&& screen == null
			&& action == null;
	}

	@Converter(EnumStringConverter.class)
	public enum Access
	{

		@Icon("2037")
		@Color("#006600")
		@Name("Permitir")
		GRANT,
		@Icon("2038")
		@Color("#660000")
		@Name("Bloquear")
		BLOCK
	}

	@Converter(EnumStringConverter.class)
	public enum Scope
	{
		@Icon("2006")
		@Name("Público")
		PUBLIC,
		@Icon("2000")
		@Name("Privado")
		PRIVATE
	}

	public boolean blocked(String module, String screen, String action)
	{
		return this.access == Access.BLOCK
			&& (this.module == null || this.module.equals(module))
			&& (this.screen == null || this.screen.equals(screen))
			&& (this.action == null || this.action.equals(action));
	}

	public boolean granted(String module, String screen, String action)
	{
		return this.access == Access.GRANT
			&& (module == null || this.module == null || this.module.equals(module))
			&& (screen == null || this.screen == null || this.screen.equals(screen))
			&& (action == null || this.action == null || this.action.equals(action));
	}
}
