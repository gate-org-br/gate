package gate.entity;

public class Bond
{

	private Role role;
	private User user;
	private Func func;

	public Role getRole()
	{
		if (role == null)
			role = new Role();
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public User getUser()
	{
		if (user == null)
			user = new User();
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public Func getFunc()
	{
		if (func == null)
			func = new Func();
		return func;
	}

	public void setFunc(Func func)
	{
		this.func = func;
	}

}
