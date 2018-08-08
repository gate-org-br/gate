package gate;

import gate.entity.Auth;
import gate.entity.Role;
import gate.entity.User;
import gate.error.ConstraintViolationException;
import gate.error.InvalidUsernameException;
import gate.sql.Cursor;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.type.ID;
import gate.type.Phone;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

class GateDao extends gate.base.Dao
{

	public GateDao()
	{
		super("Gate");
	}

	public Optional<User> getUser(String username) throws InvalidUsernameException
	{
		return getLink().from(Select.expression("Uzer.id").as("id")
				.expression("Uzer.userID").as("userID")
				.expression("Uzer.passwd").as("passwd")
				.expression("Uzer.name").as("name")
				.expression("Uzer.Role$id").as("role.id")
				.expression("Uzer.active").as("active")
				.expression("Uzer.CPF").as("CPF")
				.expression("Uzer.birthdate").as("birthdate")
				.expression("Uzer.sex").as("sex")
				.expression("Uzer.phone").as("phone")
				.expression("Uzer.cellPhone").as("cellPhone")
				.expression("Uzer.email").as("email")
				.expression("Uzer.registration").as("registration")
				.expression("Auth.id").as("auth.id")
				.expression("Auth.mode").as("auth.mode")
				.expression("Auth.type").as("auth.type")
				.expression("Auth.module").as("auth.module")
				.expression("Auth.screen").as("auth.screen")
				.expression("Auth.action").as("auth.action")
				.from("Uzer")
				.leftJoin("Auth").on(Condition.of("Uzer.id").isEq("Auth.Uzer$id"))
				.where(Condition.of("Uzer.userID")
						.eq(username).or("email").eq(username))).fetch((Cursor cursor) ->
		{
			if (!cursor.next())
				return Optional.empty();
			User user = new User()
					.setId(cursor.getValue(ID.class, "id"))
					.setActive(cursor.getValue(Boolean.class, "active"))
					.setUserID(cursor.getValue(String.class, "userID"))
					.setPasswd(cursor.getValue(String.class, "passwd"))
					.setName(cursor.getValue(String.class, "name"))
					.setEmail(cursor.getValue(String.class, "email"))
					.setPhone(cursor.getValue(Phone.class, "phone"))
					.setCellPhone(cursor.getValue(Phone.class, "cellPhone"))
					.setRole(new Role().setId(cursor.getValue(ID.class, "role.id")));
			do
			{
				Auth auth = new Auth()
						.setUser(user)
						.setId(cursor.getValue(ID.class, "auth.id"))
						.setMode(cursor.getValue(Auth.Mode.class, "auth.mode"))
						.setType(cursor.getValue(Auth.Type.class, "auth.type"))
						.setModule(cursor.getValue(String.class, "auth.module"))
						.setScreen(cursor.getValue(String.class, "auth.screen"))
						.setAction(cursor.getValue(String.class, "auth.action"));
				if (auth.getId() != null)
					user.getAuths().add(auth);
			} while (cursor.next());
			return Optional.of(user);
		});
	}

	public Role getRole(ID id)
	{
		return getLink().from("select Role.id as id, Role.active as active, Role.master as master, Role.Role$id as 'role.id', Role.roleID as roleID, Role.name as name, Role.email as email, Role.description as description, Manager.id as 'manager.id', Manager.name as 'manager.name', Auth.id as 'auth.id', Auth.mode as 'auth.mode', Auth.type as 'auth.type', Auth.module as 'auth.module', Auth.screen as 'auth.screen', Auth.action as 'auth.action' from Role left join gate.Uzer as Manager on Role.Manager$id = Manager.id left join Auth on Role.id = Auth.Role$id order by Role.name, Role.id")
				.constant()
				.fetch((gate.sql.Cursor cursor) ->
				{
					Collection<Role> roles = new ArrayList<>();
					if (cursor.next())
						do
						{
							Role role = new Role()
									.setId(cursor.getValue(ID.class, "id"))
									.setName(cursor.getValue(String.class, "name"))
									.setEmail(cursor.getValue(String.class, "email"))
									.setRoleID(cursor.getValue(String.class, "roleID"))
									.setMaster(cursor.getValue(Boolean.class, "master"))
									.setActive(cursor.getValue(Boolean.class, "active"))
									.setRole(new Role()
											.setId(cursor.getValue(ID.class, "role.id"))
											.setDescription(cursor.getValue(String.class, "description"))
											.setManager(new User()
													.setId(cursor.getValue(ID.class, "manager.id"))
													.setName(cursor.getValue(String.class, "manager.name"))));
							do
							{
								Auth auth = new Auth()
										.setRole(role)
										.setId(cursor.getValue(ID.class, "auth.id"))
										.setMode(cursor.getValue(Auth.Mode.class, "auth.mode"))
										.setType(cursor.getValue(Auth.Type.class, "auth.type"))
										.setModule(cursor.getValue(String.class, "auth.module"))
										.setScreen(cursor.getValue(String.class, "auth.screen"))
										.setAction(cursor.getValue(String.class, "auth.action"));
								if (auth.getId() != null)
									role.getAuths().add(auth);
							} while (cursor.next() && cursor.getValue(ID.class, "id").equals(role.getId()));
							roles.add(role);
						} while (!cursor.isAfterLast());
					for (Role p : roles)
						for (Role c : roles)
							if (p.equals(c.getRole()))
								p.getRoles().add(c.setRole(p));
					return roles.stream().filter((e) -> e.getId().equals(id)).findAny().orElse(null);
				});
	}

	public boolean update(User user) throws ConstraintViolationException
	{
		return getLink().prepare("update Uzer set passwd = MD5(?) where userId = ? and passwd = MD5(?)").parameters(user.getChange(), user.getUserID(), user.getPasswd()).execute() > 0;
	}

}
