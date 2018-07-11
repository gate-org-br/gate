package gateconsole.dao;

import gate.base.Dao;
import gate.sql.Link;
import gate.entity.Auth;
import gate.error.AppException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
				.properties("id", "role.id", "user.id", "+mode", "+type", "+module", "+screen", "+action")
				.parameters();
	}

	public Collection<Auth> search(Auth filter)
	{
		return getLink()
				.search(Auth.class)
				.properties("=role.id", "=user.id", "id", "+mode", "+type", "+module", "+screen", "+action")
				.matching(filter);
	}

	public Optional<Auth> select(Auth filter)
	{
		return getLink()
				.select(Auth.class)
				.properties("=id", "role.id", "user.id", "mode", "type", "module", "screen", "action")
				.matching(filter);
	}

	public void insert(Auth value) throws AppException
	{
		getLink()
				.insert(Auth.class)
				.properties("role.id", "user.id", "mode", "type", "module", "screen", "action")
				.execute(value);
	}

	public boolean update(Auth value) throws AppException
	{
		return getLink()
				.update(Auth.class)
				.properties("=id", "mode", "type", "module", "screen", "action")
				.execute(value) > 0;
	}

	public boolean delete(Auth... values) throws AppException
	{
		return getLink()
				.delete(Auth.class)
				.execute(values) > 0;
	}
}
