package gate.base;

import gate.error.NotFoundException;
import gate.lang.property.Entity;
import gate.sql.Link;
import gate.sql.delete.Delete;
import gate.sql.insert.Insert;
import gate.sql.select.Select;
import gate.sql.update.Update;
import gate.type.ID;
import java.util.List;

public class CrudDao<T> extends Dao implements Crud<T>
{

	private final Class<T> type;

	public CrudDao(Class<T> type)
	{
		this.type = type;
	}

	public CrudDao(Class<T> type, Link link)
	{
		super(link);
		this.type = type;
	}

	public CrudDao(Class<T> type, String datasource)
	{
		super(datasource);
		this.type = type;
	}

	@Override
	public List<T> search(T filter)
	{
		return getLink().search(type)
			.properties(Entity.getFullGQN(type))
			.matching(filter);
	}

	@Override
	public T select(ID id) throws NotFoundException
	{
		return Select.from(type).build()
			.parameters(id)
			.connect(getLink())
			.fetchEntity(type)
			.orElseThrow(NotFoundException::new);
	}

	@Override
	public void insert(T value)
	{
		Insert.into(type)
			.build()
			.connect(getLink())
			.value(value);
	}

	@Override
	public void update(T value)
	{
		Update.type(type)
			.build()
			.connect(getLink())
			.value(value);
	}

	@Override
	public void delete(T value)
	{
		Delete.from(type)
			.build()
			.connect(getLink())
			.value(value);
	}

}
