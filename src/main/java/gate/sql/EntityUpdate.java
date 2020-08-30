package gate.sql;

import gate.error.AppException;
import gate.error.NotFoundException;
import gate.lang.property.Entity;
import gate.sql.condition.Condition;
import gate.sql.update.TableUpdate;
import gate.sql.update.Update;
import gate.type.ID;

public abstract class EntityUpdate
{

	private final ID id;
	private final Link link;
	private TableUpdate.Compiled update;

	protected EntityUpdate(Link link, String table, ID id)
	{
		this.id = id;
		this.link = link;
		this.update = Update.table(table).compiled();
	}

	protected EntityUpdate(Link link, Class<?> type, ID id)
	{
		this.id = id;
		this.link = link;
		this.update = Update.table(Entity.getFullTableName(type)).compiled();
	}

	protected EntityUpdate set(String field, Object value)
	{
		update = update.set(field, value);
		return this;
	}

	public void execute() throws AppException
	{
		if (update.where(Condition.of("id").eq(id))
			.build()
			.connect(link)
			.execute() == 0)
			throw new NotFoundException();
	}
}
