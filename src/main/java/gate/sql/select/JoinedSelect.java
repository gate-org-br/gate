package gate.sql.select;

import gate.sql.Clause;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.GenericCondition;
import java.util.stream.Stream;

public abstract class JoinedSelect implements Clause
{

	private final Clause clause;

	public JoinedSelect(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	/**
	 * Defines the condition of the current join clause.
	 *
	 * @param predicate the {@link gate.sql.condition.Predicate} to be associated with the current join clause
	 *
	 * @return the current builder, for chained invocations
	 */
	public abstract SelectedSelect on(ConstantCondition predicate);

	public abstract static class Constant extends JoinedSelect implements Aliasable
	{

		public Constant(Clause clause)
		{
			super(clause);
		}

		@Override
		public SelectedSelect.Constant on(ConstantCondition predicate)
		{
			return new SelectedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " on " + predicate.toString();
				}
			};
		}

		public SelectedSelect.Generic on(GenericCondition predicate)
		{
			return new SelectedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " on " + predicate.toString();
				}
			};
		}

		public SelectedSelect.Compiled on(CompiledCondition predicate)
		{
			return new SelectedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " on " + predicate.toString();
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(), predicate.getParameters());
				}
			};
		}

		@Override
		public Aliased.Constant as(String alias)
		{
			return new Aliased.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " as " + alias;
				}
			};
		}
	}

	public abstract static class Generic extends JoinedSelect implements Aliasable
	{

		public Generic(Clause clause)
		{
			super(clause);
		}

		@Override
		public SelectedSelect.Generic on(ConstantCondition predicate)
		{
			return new SelectedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " on " + predicate.toString();
				}
			};
		}

		public SelectedSelect.Generic on(GenericCondition predicate)
		{
			return new SelectedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " on " + predicate.toString();
				}
			};
		}

		@Override
		public Aliased.Generic as(String alias)
		{
			return new Aliased.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " as " + alias;
				}
			};
		}
	}

	public abstract static class Compiled extends JoinedSelect implements Aliasable
	{

		public Compiled(Clause clause)
		{
			super(clause);
		}

		@Override
		public SelectedSelect.Compiled on(ConstantCondition predicate)
		{
			return new SelectedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " on " + predicate.toString();
				}
			};
		}

		public SelectedSelect.Compiled on(CompiledCondition predicate)
		{
			return new SelectedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " on " + predicate.toString();
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(), predicate.getParameters());
				}
			};
		}

		@Override
		public Aliased.Compiled as(String alias)
		{
			return new Aliased.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " as " + alias;
				}
			};
		}
	}

	public static abstract class Aliased extends JoinedSelect
	{

		public Aliased(Clause clause)
		{
			super(clause);
		}

		public abstract static class Constant extends Aliased
		{

			public Constant(Clause clause)
			{
				super(clause);
			}

			@Override
			public SelectedSelect.Constant on(ConstantCondition predicate)
			{
				return new SelectedSelect.Constant(this)
				{
					@Override
					public String toString()
					{
						return getClause() + " on " + predicate.toString();
					}
				};
			}
		}

		public abstract static class Generic extends Aliased
		{

			public Generic(Clause clause)
			{
				super(clause);
			}

			@Override
			public SelectedSelect.Generic on(ConstantCondition predicate)
			{
				return new SelectedSelect.Generic(this)
				{
					@Override
					public String toString()
					{
						return getClause() + " on " + predicate.toString();
					}
				};
			}
		}

		public abstract static class Compiled extends Aliased
		{

			public Compiled(Clause clause)
			{
				super(clause);
			}

			@Override
			public SelectedSelect.Compiled on(ConstantCondition predicate)
			{
				return new SelectedSelect.Compiled(this)
				{
					@Override
					public String toString()
					{
						return getClause() + " on " + predicate.toString();
					}
				};
			}
		}
	}

}
