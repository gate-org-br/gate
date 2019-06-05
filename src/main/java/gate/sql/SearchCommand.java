package gate.sql;

import gate.error.ConstraintViolationException;
import gate.error.FKViolationException;
import gate.error.UKViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SearchCommand implements AutoCloseable
{

	private Integer maxRows;
	private final Link c;
	private Command ps;
	private Integer queryTimeout;
	private final SearchSQL sql = new SearchSQL();

	public SearchCommand or(String field)
	{
		sql.or(field);
		return this;
	}

	public SearchCommand and(String field)
	{
		sql.and(field);
		return this;
	}

	public SearchCommand or(SearchSQL sql)
	{
		this.sql.or(sql);
		return this;
	}

	public SearchCommand and(SearchSQL sql)
	{
		this.sql.and(sql);
		return this;
	}

	public SearchCommand limit(int pageSize)
	{
		this.sql.limit(pageSize);
		return this;
	}

	public SearchCommand limit(int pageSize, int pageIndx)
	{
		this.sql.limit(pageSize, pageIndx);
		return this;
	}

	public SearchCommand $$(String comparator, Object value)
	{
		sql.$$(comparator, value);
		return this;
	}

	public SearchCommand eq(Object value)
	{
		sql.eq(value);
		return this;
	}

	public SearchCommand ne(Object value)
	{
		sql.ne(value);
		return this;
	}

	public SearchCommand gt(Object value)
	{
		sql.gt(value);
		return this;
	}

	public SearchCommand lt(Object value)
	{
		sql.lt(value);
		return this;
	}

	public SearchCommand ge(Object value)
	{
		sql.ge(value);
		return this;
	}

	public SearchCommand le(Object value)
	{
		sql.le(value);
		return this;
	}

	public SearchCommand lk(String value)
	{
		sql.lk(value);
		return this;
	}

	public SearchCommand rx(String value)
	{
		sql.rx(value);
		return this;
	}

	public SearchCommand rx(Pattern value)
	{
		sql.rx(value);
		return this;
	}

	public SearchCommand bw(Object value)
	{
		sql.bw(value);
		return this;
	}

	public SearchCommand rl(String value)
	{
		sql.rl(value);
		return this;
	}

	public SearchCommand ll(String value)
	{
		sql.ll(value);
		return this;
	}

	public <T> SearchCommand in(List<T> values)
	{
		sql.in(values);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> SearchCommand in(T... values)
	{
		sql.in(values);
		return this;
	}

	public SearchCommand isNull()
	{
		sql.isNull();
		return this;
	}

	public SearchCommand isNotNull()
	{
		sql.isNotNull();
		return this;
	}

	SearchCommand(Link c, String string, Object... parameters)
	{
		this.c = c;
		sql.apd(string);
		Arrays.asList(parameters).forEach(sql::add);
	}

	public void setMaxRows(Integer maxRows)
	{
		this.maxRows = maxRows;
	}

	public Integer getMaxRows()
	{
		return maxRows;
	}

	public Link getConnection()
	{
		return c;
	}

	public SearchCommand append(String string)
	{
		sql.apd(string);
		return this;
	}

	public SearchCommand setParameter(Object value)
	{
		sql.add(value);
		return this;
	}

	public SearchCommand orderBy(List<String> values)
	{
		sql.orderBy(values);
		return this;
	}

	public SearchCommand orderBy(String... values)
	{
		sql.orderBy(values);
		return this;
	}

	public SearchCommand groupBy(List<String> values)
	{
		sql.groupBy(values);
		return this;
	}

	public SearchCommand groupBy(String... values)
	{
		sql.groupBy(values);
		return this;
	}

	public Integer getQueryTimeout()
	{
		return queryTimeout;
	}

	public void setQueryTimeout(Integer queryTimeout)
	{
		this.queryTimeout = queryTimeout;
	}

	public Cursor getCursor()
	{
		ps = c.createCommand(sql.toString());
		if (maxRows != null)
			ps.setMaxRows(maxRows);
		if (queryTimeout != null)
			ps.setQueryTimeout(queryTimeout);
		sql.getValues().forEach(e -> ps.setParameter(e.getClass(), e));
		return ps.getCursor();
	}

	public int executeUpdate() throws ConstraintViolationException
	{
		ps = c.createCommand(sql.toString());
		if (maxRows != null)
			ps.setMaxRows(maxRows);
		if (queryTimeout != null)
			ps.setQueryTimeout(queryTimeout);
		sql.getValues().forEach(e -> ps.setParameter(e.getClass(), e));
		return ps.execute();
	}

	@Override
	public void close()
	{
		if (ps != null)
			ps.close();
	}

	@Override
	public String toString()
	{
		return sql.toString();
	}
}
