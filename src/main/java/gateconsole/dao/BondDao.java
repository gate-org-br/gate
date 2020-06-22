package gateconsole.dao;

import gate.base.Dao;
import gate.entity.Bond;
import gate.sql.Link;
import java.util.List;

public class BondDao extends Dao
{

	public BondDao()
	{
		super("Gate");
	}

	public BondDao(Link link)
	{
		super(link);
	}

	public List<Bond> search()
	{
		return getLink()
			.from(getClass().getResource("BondDao/search().sql"))
			.constant()
			.fetchEntityList(Bond.class);
	}
}
