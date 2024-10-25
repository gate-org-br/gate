package gate.sql;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
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
		Instance<DataSource> dataSourceInstance = CDI.current().select(DataSource.class);
		if (dataSourceInstance.isUnsatisfied())
			throw new IllegalArgumentException("Default DataSource not found");
		return dataSourceInstance.get();

	}

	static DataSource getNamedDataSource(String name)
	{
		Instance<DataSource> dataSourceInstance = CDI.current().select(DataSource.class,
			NamedLiteral.of(name));
		if (dataSourceInstance.isUnsatisfied())
			throw new IllegalArgumentException("DataSource not found: " + name);
		return dataSourceInstance.get();
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
