package gate.producer;

import gate.annotation.LinkResource;
import gate.entity.App;
import gate.error.InternalServerException;
import gate.sql.Link;
import gate.sql.LinkSource;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 *
 * @author davins
 * <p>
 * Produces and disposes Connection objects using specified data sources.
 *
 */
@Dependent
public class LinkSourceProducer
{

	@Produces
	@Dependent
	public LinkSource produceDefault(App app)
	{
		return () ->
		{
			try
			{
				Instance<DataSource> ds =
						CDI.current().select(DataSource.class);
				if (ds.isResolvable())
					return new Link(ds.get().getConnection());
				return Link.of(InitialContext
						.doLookup("java:/comp/env/" + app.getId()));
			} catch (SQLException ex)
			{
				throw new InternalServerException(ex);
			} catch (NamingException ex)
			{
				throw new InternalServerException("Data source not found: " + app.getId(), ex);
			}
		};
	}

	@Produces
	@Dependent
	@LinkResource("")
	public LinkSource produce(InjectionPoint injectionPoint)
	{
		var datasource = injectionPoint.getAnnotated()
				.getAnnotation(LinkResource.class).value();
		if (datasource == null || datasource.isBlank())
			throw new InternalServerException("No datasource specified");

		return () ->
		{

			try
			{
				Instance<DataSource> ds =
						CDI.current().select(DataSource.class,
								NamedLiteral.of(datasource));
				if (ds.isResolvable())
					return new Link(ds.get().getConnection());
				return Link.of((DataSource) InitialContext
						.doLookup("java:/comp/env/" + datasource));
			} catch (SQLException ex)
			{
				throw new InternalServerException(ex);
			} catch (NamingException e1)
			{
				throw new InternalServerException("Data source not found: " + datasource);
			}
		};
	}
}