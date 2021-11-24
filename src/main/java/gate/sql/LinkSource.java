package gate.sql;

import gate.error.DatabaseException;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public interface LinkSource
{

	public Link getLink();

	static LinkSource of()
	{
		return new LinkSourceImpl(getDefaultDataSource());
	}

	static LinkSource of(String name)
	{
		return new LinkSourceImpl(getNamedDataSource(name));
	}

	static DataSource getDefaultDataSource()
	{
		var beanManager = CDI.current().getBeanManager();
		var beans = beanManager.getBeans(DataSource.class);

		if (beans.isEmpty())
			throw new DatabaseException("No default datasource found");

		var bean = beans.stream().filter(e -> e.getQualifiers().stream()
			.noneMatch(a -> a.annotationType() == Default.class)).findFirst().orElseThrow(()
			-> new DatabaseException("No default datasource found"));

		DataSource dataSource = (DataSource) beanManager.getReference(bean, DataSource.class,
			beanManager.createCreationalContext(bean));
		return dataSource;

	}

	static DataSource getNamedDataSource(String name)
	{
		var beanManager = CDI.current().getBeanManager();
		var beans = beanManager.getBeans(DataSource.class);

		if (!beans.isEmpty())
		{
			var bean = beans.stream().filter(e -> e.getQualifiers().stream().anyMatch(a -> a.annotationType()
				== Named.class && ((Named) a).value().equals(name)))
				.findFirst().orElseThrow(() -> new DatabaseException("No " + name + " datasource found"));

			DataSource dataSource = (DataSource) beanManager.getReference(bean, DataSource.class,
				beanManager.createCreationalContext(bean));
			return dataSource;
		}

		try
		{
			return (DataSource) new InitialContext().lookup("java:/comp/env/" + name);
		} catch (NamingException ex)
		{
			throw new DatabaseException(ex);
		}
	}

	static class LinkSourceImpl implements LinkSource
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
