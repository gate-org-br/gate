package gate.base;

import gate.sql.Link;

/**
 * Base class from where data access objects can be extended.
 */
public abstract class Dao extends Base implements AutoCloseable
{

	protected final Link link;

	/**
	 * Creates a data access object associated with the current data source.
	 */
	public Dao()
	{
		this.link = new Link();
	}

	/**
	 * Creates a data access object associated with the specified link.
	 *
	 * @param link link to be associated with the new DAO
	 */
	public Dao(Link link)
	{
		this.link = link;
	}

	/**
	 * Creates a data access object associated with the specified data source name.
	 *
	 * @param datasource name of the data source to be associated with the new DAO
	 */
	public Dao(String datasource)
	{
		this.link = new Link(datasource);
	}

	/**
	 * Returns the database link associated with this data access object.
	 *
	 * @return the database link associated with this data access object
	 */
	public Link getLink()
	{
		return link;
	}

	/**
	 * Rollback any pending transaction and closes the link associated with this data access object.
	 */
	@Override
	public void close()
	{
		if (link != null)
			link.close();
	}

	public void beginTran()
	{
		link.beginTran();
	}

	public void commit()
	{
		link.commit();
	}

	public void rollback()
	{
		link.rollback();
	}
}
