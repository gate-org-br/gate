package gate.sql.select;

import gate.converter.Converter;
import gate.lang.property.Entity;
import gate.lang.property.Property;
import gate.sql.OrderBy;
import gate.sql.OrderBy.Ordering;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.Condition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.GenericCondition;
import gate.sql.condition.PropertyCondition;
import gate.sql.statement.Query;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypedSelect<T> implements Query.Builder
{

	private final Class<T> type;
	private final Set<String> sources = new LinkedHashSet<>();

	public TypedSelect(Class<T> type)
	{
		this.type = type;
		sources.add(Entity.getFullTableName(type));
	}

	public Properties property(Property property)
	{
		return new Properties().property(property);
	}

	public Properties properties(List<Property> properties)
	{
		return new Properties().properties(properties);
	}

	public Properties property(String property)
	{
		return new Properties().property(property);
	}

	public Properties properties(String... properties)
	{
		return new Properties().properties(properties);
	}

	public Query.Builder where(GenericCondition condition)
	{
		return properties(Entity.getProperties(type, e -> true)).where(condition);
	}

	public Query.Builder where(PropertyCondition condition)
	{
		return properties(Entity.getProperties(type, e -> true)).where(condition);
	}

	public Query.Constant.Builder where(ConstantCondition condition)
	{
		return properties(Entity.getProperties(type, e -> true)).where(condition);
	}

	public Query.Compiled.Builder where(CompiledCondition condition)
	{
		return properties(Entity.getProperties(type, e -> true)).where(condition);
	}

	@Override
	public Query build()
	{
		Property property = Property.getProperty(type, Entity.getId(type));
		return where(Condition.of(Entity.getFullColumnName(property))
			.isEq(property)).build();
	}

	public class Properties implements Query.Builder
	{

		final Set<String> columns = new LinkedHashSet<>();

		public Properties()
		{
		}

		public Properties property(Property property)
		{
			String name = property.toString();
			sources.addAll(Entity.getJoins(property));
			Converter converter = property.getConverter();
			String columnName = Entity.getFullColumnName(property);
			if (converter.getSufixes().isEmpty())
				columns.add(columnName + " as '" + name + "'");
			else
				converter.getSufixes().stream().map((e) -> columnName + "__" + e + " as '" + name + "__" + e + "'").forEach(columns::add);
			return this;
		}

		public Properties property(String property)
		{
			return property(Property.getProperty(type, property));
		}

		public Properties properties(List<Property> properties)
		{
			properties.forEach(this::property);
			return this;
		}

		public Properties properties(String... properties)
		{
			Stream.of(properties).forEach(this::property);
			return this;
		}

		public PropertyWhere where(PropertyCondition condition)
		{
			condition.getProperties().forEach(e -> sources.addAll(Entity.getJoins(e)));
			return new PropertyWhere(condition);
		}

		public ConstantWhere where(ConstantCondition condition)
		{
			condition.getProperties().forEach(e -> sources.addAll(Entity.getJoins(e)));
			return new ConstantWhere(condition);
		}

		public GenericWhere where(GenericCondition condition)
		{
			condition.getProperties().forEach(e -> sources.addAll(Entity.getJoins(e)));
			return new GenericWhere(condition);
		}

		public CompiledWhere where(CompiledCondition condition)
		{
			condition.getProperties().forEach(e -> sources.addAll(Entity.getJoins(e)));
			return new CompiledWhere(condition);
		}

		public Query.Builder orderBy(Ordering ordering)
		{
			ordering.getColumns().forEach(e -> sources.addAll(Entity.getJoins(Property.getProperty(type, e))));
			return () -> Query.of(this + " order by " + ordering.toString(e -> Entity.getFullColumnNames(type, e)));
		}

		@Override
		public Query build()
		{
			Property property = Property.getProperty(type, Entity.getId(type));
			return where(Condition.of(Entity.getFullColumnName(property))
				.isEq(property)).build();
		}

		@Override
		public String toString()
		{
			return "select " + String.join(", ", columns) + " from " + String.join(" ", sources);
		}

		public class ConstantWhere implements Query.Constant.Builder
		{

			public final ConstantCondition condition;

			public ConstantWhere(ConstantCondition condition)
			{
				this.condition = condition;
			}

			public Query.Constant.Builder orderBy(Ordering ordering)
			{
				ordering.getColumns().forEach(e -> sources.addAll(Entity.getJoins(Property.getProperty(type, e))));
				return () -> Query.of(ConstantWhere.this + " order by " + ordering.toString(e -> Entity.getFullColumnNames(type, e))).constant();
			}

			@Override
			public Query.Constant build()
			{
				return Query.of(toString()).constant();
			}

			@Override
			public String toString()
			{
				return Properties.this + " where " + condition;
			}
		}

		public class PropertyWhere implements Query.Builder
		{

			public final PropertyCondition condition;

			public PropertyWhere(PropertyCondition condition)
			{
				this.condition = condition;
			}

			public Query.Builder orderBy(OrderBy.Ordering ordering)
			{
				ordering.getColumns().forEach(e -> sources.addAll(Entity.getJoins(Property.getProperty(type, e))));
				return () -> Query.of(PropertyWhere.this + " order by " + ordering.toString(e -> Entity.getFullColumnNames(type, e)));
			}

			@Override
			public Query build()
			{
				return Query.of(toString());
			}

			@Override
			public String toString()
			{
				return Properties.this + " where " + condition;
			}
		}

		public class GenericWhere implements Query.Builder
		{

			public final GenericCondition condition;

			public GenericWhere(GenericCondition condition)
			{
				this.condition = condition;
			}

			public Query.Builder orderBy(OrderBy.Ordering ordering)
			{
				ordering.getColumns().forEach(e -> sources.addAll(Entity.getJoins(Property.getProperty(type, e))));
				return () -> Query.of(GenericWhere.this + " order by " + ordering.toString(e -> Entity.getFullColumnNames(type, e)));
			}

			@Override
			public Query build()
			{
				return Query.of(toString());
			}

			@Override
			public String toString()
			{
				return Properties.this + " where " + condition;
			}
		}

		public class CompiledWhere implements Query.Compiled.Builder
		{

			public final CompiledCondition condition;

			public CompiledWhere(CompiledCondition condition)
			{
				this.condition = condition;
			}

			public Query.Compiled.Builder orderBy(Ordering ordering)
			{
				ordering.getColumns().forEach(e -> sources.addAll(Entity.getJoins(Property.getProperty(type, e))));
				return () -> Query.of(CompiledWhere.this + " order by " + ordering.toString(e -> Entity.getFullColumnNames(type, e)))
					.parameters(condition.getParameters().collect(Collectors.toList()));

			}

			@Override
			public Query.Compiled build()
			{
				return Query.of(toString()).parameters(condition.getParameters().collect(Collectors.toList()));
			}

			@Override
			public String toString()
			{
				return Properties.this + " where " + condition;
			}
		}
	}
}
