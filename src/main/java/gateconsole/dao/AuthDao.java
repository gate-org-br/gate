package gateconsole.dao;

import gate.base.Dao;
import gate.sql.Link;
import gate.entity.Auth;
import gate.error.AppException;
import gate.error.NotFoundException;
import gate.type.ID;

import java.util.Collection;
import java.util.List;

public class AuthDao extends Dao
{

	public AuthDao()
	{
		super("Gate");
	}

	public AuthDao(Link link)
	{
		super(link);
	}

	public List<Auth> search()
	{
		return getLink()
			.search(Auth.class)
			.properties("id", "role.id", "user.id", "func.id",
				"+mode", "+type", "+module", "+screen", "+action")
			.parameters();
	}

	public Collection<Auth> search(Auth filter)
	{
		return getLink()
			.search(Auth.class)
			.properties("=role.id", "=user.id", "=func.id", "id", "+mode", "+type", "+module", "+screen", "+action")
			.matching(filter);
	}

	public Auth select(ID id) throws NotFoundException
	{
		return getLink()
			.select(Auth.class)
			.properties("=id", "role.id", "user.id", "func.id", "mode", "type", "module", "screen", "action")
			.parameters(id)
			.orElseThrow(NotFoundException::new);
	}

	public void insert(Auth value) throws AppException
	{
		getLink()
			.insert(Auth.class)
			.properties("role.id", "user.id", "func.id", "mode", "type", "module", "screen", "action")
			.execute(value);
	}

	public void update(Auth value) throws AppException
	{
		if (getLink()
			.update(Auth.class)
			.properties("=id", "mode", "type", "module", "screen", "action")
			.execute(value) == 0)
			throw new NotFoundException();
	}

	public void delete(Auth... values) throws AppException
	{
		if (getLink()
			.delete(Auth.class)
			.execute(values) == 0)
			throw new NotFoundException();
	}
}
