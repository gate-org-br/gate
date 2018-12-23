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

	public FuncDao(Link c)
	{
		super(c);
	}

	public List<Func> search(Func func) throws AppException
	{
		return getLink()
			.search(Func.class)
			.properties("=id", "%name")
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

	public void add(User user, Func func) throws AppException
	{
		Insert.into("UserFunc")
			.set("User$id", user.getId())
			.set("Func$id", func.getId())
			.build().connect(getLink())
			.execute();
	}

	public void add(Role role, Func func) throws AppException
	{
		Insert.into("RoleFunc")
			.set("Role$id", role.getId())
			.set("Func$id", func.getId())
			.build().connect(getLink())
			.execute();
	}

	public void rem(User user, Func func) throws AppException
	{
		Delete.from("UserFunc")
			.where(Condition.of("Uzer$id")
				.eq(ID.class, user.getId())
				.and("Func$id").eq(ID.class, func.getId()))
			.build().connect(getLink())
			.execute();
	}

	public void rem(Role role, Func func) throws AppException
	{
		Delete.from("RoleFunc")
			.where(Condition.of("Role$id")
				.eq(ID.class, role.getId())
				.and("Func$id").eq(ID.class, func.getId()))
			.build().connect(getLink())
			.execute();
	}
}
