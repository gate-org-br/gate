package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Stream;

public interface Joinable extends Clause
{

	JoinedSelect join(String exp);

	JoinedSelect leftJoin(String exp);

	JoinedSelect rightJoin(String exp);

	JoinedSubqueryAlias join(Query.Constant query);

	JoinedSubqueryAlias join(Query.Constant.Builder query);

	JoinedSubqueryAlias leftJoin(Query.Constant query);

	JoinedSubqueryAlias leftJoin(Query.Constant.Builder query);

	JoinedSubqueryAlias rightJoin(Query.Constant query);

	JoinedSubqueryAlias rightJoin(Query.Constant.Builder query);

	interface Constant extends Joinable
	{

		@Override
		default JoinedSelect.Constant join(String exp)
		{
			return new JoinedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " join " + exp;
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Constant join(Query.Constant query)
		{
			return new JoinedSubqueryAlias.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " join (" + query.toString() + ")";
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Constant join(Query.Constant.Builder query)
		{
			return join(query.build());
		}

		default JoinedSubqueryAlias.Generic join(Query query)
		{
			return new JoinedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " join (" + query.toString() + ")";
				}
			};
		}

		default JoinedSubqueryAlias.Generic join(Query.Builder query)
		{
			return join(query.build());
		}

		default JoinedSubqueryAlias.Compiled join(Query.Compiled query)
		{
			return new JoinedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " join (" + query.toString() + ")";
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(),
							query.getParameters().stream());
				}
			};
		}

		default JoinedSubqueryAlias.Compiled join(Query.Compiled.Builder query)
		{
			return join(query.build());
		}

		@Override
		default JoinedSelect.Constant leftJoin(String exp)
		{
			return new JoinedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " left join " + exp;
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Constant leftJoin(Query.Constant query)
		{
			return new JoinedSubqueryAlias.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " left join (" + query.toString() + ")";
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Constant leftJoin(Query.Constant.Builder query)
		{
			return leftJoin(query.build());
		}

		default JoinedSubqueryAlias.Generic leftJoin(Query query)
		{
			return new JoinedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " left join (" + query.toString() + ")";
				}
			};
		}

		default JoinedSubqueryAlias.Generic leftJoin(Query.Builder query)
		{
			return leftJoin(query.build());
		}

		default JoinedSubqueryAlias.Compiled leftJoin(Query.Compiled query)
		{
			return new JoinedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " left join (" + query.toString() + ")";
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(),
							query.getParameters().stream());
				}
			};
		}

		default JoinedSubqueryAlias.Compiled leftJoin(Query.Compiled.Builder query)
		{
			return leftJoin(query.build());
		}

		@Override
		default JoinedSelect.Constant rightJoin(String exp)
		{
			return new JoinedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " right join " + exp;
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Constant rightJoin(Query.Constant query)
		{
			return new JoinedSubqueryAlias.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " right join (" + query.toString() + ")";
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Constant rightJoin(Query.Constant.Builder query)
		{
			return rightJoin(query.build());
		}

		default JoinedSubqueryAlias.Generic rightJoin(Query query)
		{
			return new JoinedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " right join (" + query.toString() + ")";
				}
			};
		}

		default JoinedSubqueryAlias.Generic rightJoin(Query.Builder query)
		{
			return rightJoin(query.build());
		}

		default JoinedSubqueryAlias.Compiled rightJoin(Query.Compiled query)
		{
			return new JoinedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " right join (" + query.toString() + ")";
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(),
							query.getParameters().stream());
				}
			};
		}

		default JoinedSubqueryAlias.Compiled rightJoin(Query.Compiled.Builder query)
		{
			return rightJoin(query.build());
		}
	}

	interface Generic extends Joinable
	{

		@Override
		default JoinedSelect.Generic join(String exp)
		{
			return new JoinedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " join " + exp;
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Generic join(Query.Constant query)
		{
			return new JoinedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " join (" + query.toString() + ")";
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Generic join(Query.Constant.Builder query)
		{
			return join(query.build());
		}

		default JoinedSubqueryAlias.Generic join(Query query)
		{
			return new JoinedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " join (" + query.toString() + ")";
				}
			};
		}

		default JoinedSubqueryAlias.Generic join(Query.Builder query)
		{
			return join(query.build());
		}

		@Override
		default JoinedSelect.Generic leftJoin(String exp)
		{
			return new JoinedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " left join " + exp;
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Generic leftJoin(Query.Constant query)
		{
			return new JoinedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " left join (" + query.toString() + ")";
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Generic leftJoin(Query.Constant.Builder query)
		{
			return leftJoin(query.build());
		}

		default JoinedSubqueryAlias.Generic leftJoin(Query query)
		{
			return new JoinedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " left join (" + query.toString() + ")";
				}
			};
		}

		default JoinedSubqueryAlias.Generic leftJoin(Query.Builder query)
		{
			return leftJoin(query.build());
		}

		@Override
		default JoinedSelect.Generic rightJoin(String exp)
		{
			return new JoinedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " right join " + exp;
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Generic rightJoin(Query.Constant query)
		{
			return new JoinedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " right join (" + query.toString() + ")";
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Generic rightJoin(Query.Constant.Builder query)
		{
			return rightJoin(query.build());
		}

		default JoinedSubqueryAlias.Generic rightJoin(Query query)
		{
			return new JoinedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " right join (" + query.toString() + ")";
				}
			};
		}

		default JoinedSubqueryAlias.Generic rightJoin(Query.Builder query)
		{
			return leftJoin(query.build());
		}

	}

	interface Compiled extends Joinable
	{

		@Override
		default JoinedSelect.Compiled join(String exp)
		{
			return new JoinedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " join " + exp;
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Compiled join(Query.Constant query)
		{
			return new JoinedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " join (" + query.toString() + ")";
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Compiled join(Query.Constant.Builder query)
		{
			return rightJoin(query.build());
		}

		default JoinedSubqueryAlias.Compiled join(Query.Compiled query)
		{
			return new JoinedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " join (" + query.toString() + ")";
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(),
							query.getParameters().stream());
				}
			};
		}

		default JoinedSubqueryAlias.Compiled join(Query.Compiled.Builder query)
		{
			return join(query.build());
		}

		@Override
		default JoinedSelect.Compiled leftJoin(String exp)
		{
			return new JoinedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " left join " + exp;
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Compiled leftJoin(Query.Constant query)
		{
			return new JoinedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " left join (" + query.toString() + ")";
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Compiled leftJoin(Query.Constant.Builder query)
		{
			return rightJoin(query.build());
		}

		default JoinedSubqueryAlias.Compiled leftJoin(Query.Compiled query)
		{
			return new JoinedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " left join (" + query.toString() + ")";
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(),
							query.getParameters().stream());
				}
			};
		}

		default JoinedSubqueryAlias.Compiled leftJoin(Query.Compiled.Builder query)
		{
			return join(query.build());
		}

		@Override
		default JoinedSelect.Compiled rightJoin(String exp)
		{
			return new JoinedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " right join " + exp;
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Compiled rightJoin(Query.Constant query)
		{
			return new JoinedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " right join (" + query.toString() + ")";
				}
			};
		}

		@Override
		default JoinedSubqueryAlias.Compiled rightJoin(Query.Constant.Builder query)
		{
			return rightJoin(query.build());
		}

		default JoinedSubqueryAlias.Compiled rightJoin(Query.Compiled query)
		{
			return new JoinedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " right join (" + query.toString() + ")";
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(),
							query.getParameters().stream());
				}
			};
		}

		default JoinedSubqueryAlias.Compiled rightJoin(Query.Compiled.Builder query)
		{
			return join(query.build());
		}
	}
}
