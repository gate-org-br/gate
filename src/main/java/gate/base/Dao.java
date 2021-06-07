package gate.base;

import gate.sql.Link;
import java.util.Objects;

/**
 * Base class from where data access objects can be extended.
 */
public abstract class Dao extends Base implements AutoCloseable
{

	private final Link link;
	private final boolean created;

	/**
	 * Creates a data access object associated with the current data source.
	 */
	public Dao()
	{
		created = true;
		link = new Link();

	}

	/**
	 * Creates a data access object associated with the specified link.
	 *
	 * @param link link to be associated with the new DAO
	 */
	public Dao(Link link)
	{
		created = false;
		this.link = Objects.requireNonNull(link);
	}

	/**
	 * Creates a data access object associated with the specified data source name.
	 *
	 * @param datasource name of the data source to be associated with the new DAO
	 */
	public Dao(String datasource)
	{
		created = true;
		this.link = new Link(Objects.requireNonNull(datasource));
	}

	/**
	 * Creates a Dao for the specified database.
	 *
	 * @param driver driver to be used
	 * @param url URL where to connect
	 * @param username user to be used on connection
	 * @param password password to be used on connection
	 */
	public Dao(String driver, String url, String username, String password)
	{
		created = true;
		this.link = new Link(driver, url, username, password);
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
	 * Closes any link created by this data access object.
	 */
	@Override
	public void close()
	{
		if (created && !link.isClosed())
			link.close();
	}

	/**
	 * Starts a new transaction.
	 */
	public void beginTran()
	{
		link.beginTran();
	}

	/**
	 * Commits the current transaction.
	 */
	public void commit()
	{
		link.commit();
	}

	/**
	 * Rollback the current transaction.
	 */
	public void rollback()
	{
		link.rollback();
	}
}
