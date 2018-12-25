package gateconsole.dao;

import gate.base.Dao;
import gate.entity.Func;
import gate.entity.Role;
import gate.entity.User;
import gate.error.AppException;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.condition.Condition;
import gate.sql.delete.Delete;
import gate.sql.insert.Insert;
import gate.type.ID;
import java.util.List;

public class FuncDao extends Dao
{

	public FuncDao()
	{
		super("Gate");
	}

	public FuncDao(Link link)
	{
		super(link);
	}

	public List<Func> search()
	{
		return getLink()
			.from("select id, name from Func order by name")
			.constant()
			.fetchEntityList(Func.class);
	}

	public List<Func> search(Func func)
	{
		return getLink()
			.search(Func.class)
			.properties("=id", "%+name")
			.matching(func);
	}

	public Func select(ID id) throws NotFoundException
	{
		return getLink()
			.select(Func.class)
			.properties("=id", "name")
			.parameters(id)
			.orElseThrow(NotFoundException::new);
	}

	public void insert(Func value) throws AppException
	{
		getLink()
			.insert(Func.class)
			.execute(value);
	}

	public void update(Func value) throws AppException
	{
		if (getLink()
			.update(Func.class)
			.execute(value) == 0)
			throw new NotFoundException();
	}

	public void delete(Func value) throws AppException
	{
		if (getLink()
			.delete(Func.class)
			.execute(value) == 0)
			throw new NotFoundException();
	}

	public static class UserDao extends Dao
	{

		public List<Func> search(User user)
		{
			return getLink()
				.from(getClass().getResource("FuncDao/UserDao/search(User).sql"))
				.parameters(user.getId())
				.fetchEntityList(Func.class);
		}

		public void insert(Func func, User user) throws AppException
		{
			Insert.into("UzerFunc")
				.set("Func$id", func.getId())
				.set("Uzer$id", user.getId())
				.build().connect(getLink())
				.execute();
		}

		public void delete(Func func, User user) throws AppException
		{
			Delete.from("UzerFunc")
				.where(Condition.of("Uzer$id")
					.eq(ID.class, user.getId())
					.and("Func$id").eq(ID.class, func.getId()))
				.build().connect(getLink())
				.execute();
		}
	}

	public static class RoleDao extends Dao
	{

		public List<Func> search(Role role)
		{
			return getLink()
				.from(getClass().getResource("FuncDao/RoleDao/search(Role).sql"))
				.parameters(role.getId())
				.fetchEntityList(Func.class);
		}

		public void insert(Func func, Role role) throws AppException
		{
			Insert.into("RoleFunc")
				.set("Func$id", func.getId())
				.set("Role$id", role.getId())
				.build().connect(getLink())
				.execute();
		}

		public void delete(Func func, Role role) throws AppException
		{
			Delete.from("RoleFunc")
				.where(Condition.of("Role$id")
					.eq(ID.class, role.getId())
					.and("Func$id").eq(ID.class, func.getId()))
				.build().connect(getLink())
				.execute();
		}
	}
}
