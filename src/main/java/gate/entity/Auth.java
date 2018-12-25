package gate.entity;

import gate.annotation.Column;
import gate.annotation.Description;
import gate.annotation.Entity;
import gate.annotation.Icon;
import gate.annotation.Schema;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.constraint.Required;
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
	private Type type;

	@Required
	@Description("Defina o modo do acesso.")
	private Mode mode;

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

	public Type getType()
	{
		return type;
	}

	public Auth setType(Type type)
	{
		this.type = type;
		return this;
	}

	public Mode getMode()
	{
		return mode;
	}

	public Auth setMode(Mode mode)
	{
		this.mode = mode;
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
		return Mode.ALLOW == mode
			&& module == null
			&& screen == null
			&& action == null;
	}

	public enum Mode
	{

		@Icon("2037")
		ALLOW("Permissão"),
		@Icon("2038")
		BLOCK("Bloqueio");

		private final String string;

		Mode(String string)
		{
			this.string = string;
		}

		@Override
		public String toString()
		{
			return string;
		}
	}

	public enum Type
	{

		@Icon("2006")
		PUBLIC("Público"),
		@Icon("2000")
		PRIVATE("Privado");

		private final String string;

		Type(String string)
		{
			this.string = string;
		}

		@Override
		public String toString()
		{
			return string;
		}
	}

	public boolean blocks(String module, String screen, String action)
	{
		return this.mode == Mode.BLOCK
			&& (this.module == null || this.module.equals(module))
			&& (this.screen == null || this.screen.equals(screen))
			&& (this.action == null || this.action.equals(action));
	}

	public boolean allows(boolean strict, String module, String screen, String action)
	{
		return this.mode == Mode.ALLOW
			&& strict
				? isSuperAuth()
				|| (Objects.equals(this.module, module)
				&& Objects.equals(this.screen, screen)
				&& Objects.equals(this.action, action))
				: (module == null || this.module == null || this.module.equals(module))
				&& (screen == null || this.screen == null || this.screen.equals(screen))
				&& (action == null || this.action == null || this.action.equals(action));
	}
}
