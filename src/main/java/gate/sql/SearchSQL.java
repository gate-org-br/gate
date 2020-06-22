package gate.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SearchSQL
{

	private String clause;
	private final StringBuilder sql = new StringBuilder();
	private final List<Object> values = new ArrayList<>();

	public SearchSQL()
	{
	}

	public SearchSQL(String string)
	{
		sql.append(string);
	}

	public SearchSQL apd(String string)
	{
		sql.append(string);
		return this;
	}

	public SearchSQL add(Object value)
	{
		values.add(value);
		return this;
	}

	public List<Object> getValues()
	{
		return values;
	}

	public SearchSQL or(String field)
	{
		if (clause != null)
			throw new java.lang.IllegalStateException();
		this.clause = String.format(" or %s ", field);
		return this;
	}

	public SearchSQL and(String field)
	{
		if (clause != null)
			throw new java.lang.IllegalStateException();
		this.clause = String.format(" and %s ", field);
		return this;
	}

	public SearchSQL $$(String comparator, Object value)
	{
		if (clause == null)
			throw new java.lang.IllegalStateException();

		if (value != null)
		{
			values.add(value);
			sql.append(clause).append(comparator);
		}

		clause = null;
		return this;
	}

	public SearchSQL orderBy(List<String> values)
	{
		sql.append(" order by ");
		for (int i = 0; i < values.size(); i++)
			if (i == 0)
				sql.append(values.get(i));
			else
				sql.append(", ").append(values.get(i));
		return this;
	}

	public SearchSQL orderBy(String... values)
	{
		return orderBy(Arrays.asList(values));
	}

	public SearchSQL groupBy(List<String> values)
	{
		sql.append(" group by ");
		for (int i = 0; i < values.size(); i++)
			if (i == 0)
				sql.append(values.get(i));
			else
				sql.append(", ").append(values.get(i));
		return this;
	}

	public SearchSQL groupBy(String... values)
	{
		return groupBy(Arrays.asList(values));
	}

	public SearchSQL eq(Object value)
	{
		return $$("= ?", value);
	}

	public SearchSQL ne(Object value)
	{
		return $$("<> ?", value);
	}

	public SearchSQL gt(Object value)
	{
		return $$("> ?", value);
	}

	public SearchSQL lt(Object value)
	{
		return $$("< ?", value);
	}

	public SearchSQL ge(Object value)
	{
		return $$(">= ?", value);
	}

	public SearchSQL le(Object value)
	{
		return $$("<= ?", value);
	}

	public SearchSQL bw(Object value)
	{
		return $$("between ? and ?", value);
	}

	public SearchSQL rx(String value)
	{
		return $$("rlike ?", value);
	}

	public SearchSQL rx(Pattern value)
	{
		return $$("rlike ?", value);
	}

	public SearchSQL lk(String value)
	{
		if (value != null)
			value = String.format("%%%s%%", value);
		return $$("like ?", value);
	}

	public SearchSQL rl(String value)
	{
		if (value != null)
			value = String.format("%s%%", value);
		return $$("like ?", value);
	}

	public SearchSQL ll(String value)
	{
		if (value != null)
			value = String.format("%%%s", value);
		return $$("like ?", value);
	}

	public <T> SearchSQL in(List<T> values)
	{
		sql.append(clause).append("in (");
		for (int i = 0; i < values.size(); i++)
		{
			sql.append(i == 0 ? "?" : ", ?");
			this.values.add(values.get(i));
		}
		sql.append(")");
		clause = null;
		return this;
	}

	public <T> SearchSQL limit(Integer pageSize)
	{
		sql.append(" limit ?");
		values.add(pageSize);
		return this;
	}

	public <T> SearchSQL limit(Integer pageSize, Integer pageIndx)
	{
		sql.append(" limit ?, ?");
		values.add(pageSize * pageIndx);
		values.add(pageSize);
		return this;
	}

	public SearchSQL isNull()
	{
		sql.append(clause).append(" is null");
		clause = null;
		return this;
	}

	public SearchSQL isNotNull()
	{
		sql.append(clause).append(" is not null");
		clause = null;
		return this;
	}

	public <T> SearchSQL in(T... values)
	{
		return in(Arrays.asList(values));
	}

	public SearchSQL or(SearchSQL sql)
	{
		if (clause != null)
			throw new java.lang.IllegalStateException();
		this.sql.append(String.format(" or (%s)", sql.toString()));
		this.values.addAll(sql.getValues());
		return this;
	}

	public SearchSQL and(SearchSQL sql)
	{
		if (clause != null)
			throw new java.lang.IllegalStateException();
		this.sql.append(String.format(" and (%s)", sql.toString()));
		this.values.addAll(sql.getValues());
		return this;
	}

	@Override
	public String toString()
	{
		return sql.toString();
	}
}
