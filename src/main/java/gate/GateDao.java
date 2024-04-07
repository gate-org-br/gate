package gate;

import gate.entity.Auth;
import gate.entity.Func;
import gate.entity.Role;
import gate.entity.User;
import gate.error.ConstraintViolationException;
import gate.error.InvalidUsernameException;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.condition.Condition;
import gate.sql.update.Update;
import gate.type.CPF;
import gate.type.ID;
import gate.type.MD5;
import gate.type.Phone;
import gate.type.Sex;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class GateDao extends gate.base.Dao
{

	public GateDao(Link link)
	{
		super(link);
	}

	public User select(String username) throws InvalidUsernameException
	{
		return (User) getLink().from(getClass().getResource("select(String).sql"))
			.parameters(username, username)
			.fetch(cursor ->
			{
				if (cursor.next())
				{
					User user = new User();
					user.setId(cursor.getValue(ID.class, "id"));
					user.setActive(cursor.getValue(Boolean.class, "active"));
					user.getRole().setId(cursor.getValue(ID.class, "role.id"));
					user.setUsername(cursor.getValue(String.class, "username"));
					user.setPassword(cursor.getValue(String.class, "password"));
					user.setName(cursor.getValue(String.class, "name"));
					user.setCode(cursor.getValue(String.class, "code"));
					user.setEmail(cursor.getValue(String.class, "email"));
					user.setPhone(cursor.getValue(Phone.class, "phone"));
					user.setCellPhone(cursor.getValue(Phone.class, "cellPhone"));
					user.setCPF(cursor.getValue(CPF.class, "CPF"));
					user.setSex(cursor.getValue(Sex.class, "sex"));
					user.setBirthdate(cursor.getValue(LocalDate.class, "birthdate"));
					user.setRegistration(cursor.getValue(LocalDateTime.class, "registration"));

					do
					{
						Func func = new Func();
						func.setId(cursor.getValue(ID.class, "func.id"));
						if (func.getId() != null)
							user.getFuncs().add(func);
					} while (cursor.next());

					return Optional.of(user);
				}

				return Optional.empty();
			})
			.orElseThrow(InvalidUsernameException::new);
	}

	public List<Auth> getAuths()
	{
		return getLink()
			.from(getClass().getResource("getAuths().sql"))
			.constant()
			.fetchEntityList(Auth.class);
	}

	public List<Role> getRoles()
	{
		return getLink()
			.from(getClass().getResource("getRoles().sql"))
			.constant()
			.fetch(cursor ->
			{
				List<Role> roles = new ArrayList<>();
				if (cursor.next())
				{
					do
					{
						Role role = new Role();
						role.setId(cursor.getValue(ID.class, "id"));
						role.setActive(cursor.getValue(Boolean.class, "active"));
						role.setMaster(cursor.getValue(Boolean.class, "master"));
						role.setRolename(cursor.getValue(String.class, "rolename"));
						role.setName(cursor.getValue(String.class, "name"));
						role.setEmail(cursor.getValue(String.class, "email"));
						role.setDescription(cursor.getValue(String.class, "description"));
						role.getRole().setId(cursor.getValue(ID.class, "role.id"));
						role.getManager().setId(cursor.getValue(ID.class, "manager.id"));
						role.getManager().setName(cursor.getValue(String.class, "manager.name"));

						do
						{
							Func func = new Func();
							func.setId(cursor.getValue(ID.class, "func.id"));
							if (func.getId() != null)
								role.getFuncs().add(func);
						} while (cursor.next() && cursor.getValue(ID.class, "id").equals(role.getId()));

						roles.add(role);
					} while (!cursor.isAfterLast());
				}
				return roles;
			});
	}

	public void update(User user, String password) throws ConstraintViolationException, NotFoundException
	{
		Update.table("Uzer")
			.set("password", MD5.digest(password))
			.where(Condition.of("id").eq(user.getId()))
			.build()
			.connect(getLink()).orElseThrow(NotFoundException::new);
	}

}
