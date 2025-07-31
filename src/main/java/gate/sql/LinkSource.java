package gate.sql;

import gate.producer.AppProducer;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public interface LinkSource
{

	Link getLink();

	static LinkSource of()
	{
		return new LinkSourceImpl(getDefaultDataSource());
	}

	static LinkSource of(String name)
	{
		return new LinkSourceImpl(getNamedDataSource(name));
	}

	static LinkSource of(DataSource datasource)
	{
		return new LinkSourceImpl(datasource);
	}

	static DataSource getDefaultDataSource()
	{
		Instance<DataSource> ds
			= CDI.current().select(DataSource.class);
		if (ds.isResolvable())
			return ds.get();

		return getJNDIDataSource(CDI.current()
			.select(AppProducer.class).get().produce().getId());
	}

	static DataSource getNamedDataSource(String name)
	{
		Instance<DataSource> ds
			= CDI.current().select(DataSource.class, NamedLiteral.of(name));
		if (ds.isResolvable())
			return ds.get();

		return getJNDIDataSource(name);
	}

	private static DataSource getJNDIDataSource(String name)
	{
		try
		{
			System.out.println("java:/comp/env/" + name);
			return InitialContext.doLookup("java:/comp/env/" + name);
		} catch (NamingException e1)
		{
			throw new IllegalArgumentException("Data source not found: " + name);
		}
	}

	class LinkSourceImpl implements LinkSource
	{

		private final DataSource datasource;

		public LinkSourceImpl(DataSource datasource)
		{
			this.datasource = datasource;
		}

		@Override
		public Link getLink()
		{
			return new Link(datasource);
		}
	}

}
