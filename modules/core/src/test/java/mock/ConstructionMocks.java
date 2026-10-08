package mock;

import gate.annotation.Canonical;

import java.time.LocalDate;
import java.util.Objects;

public class ConstructionMocks
{
	public static class ConstructionMock
	{
		private final String field1;
		private final LocalDate field2;
		private final Integer field3;

		public ConstructionMock(String field1, LocalDate field2, Integer field3)
		{
			this.field1 = field1;
			this.field2 = field2;
			this.field3 = field3;
		}

		public String getField1() {return field1;}

		public LocalDate getField2() {return field2;}

		public Integer getField3() {return field3;}

		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof ConstructionMock constructionMock
			       && Objects.equals(field1, constructionMock.field1)
			       && Objects.equals(field2, constructionMock.field2)
			       && Objects.equals(field3, constructionMock.field3);
		}

		@Override
		public int hashCode() {return Objects.hash(field1, field2, field3);}
	}

	public static class BuilderMock extends ConstructionMock
	{
		private BuilderMock(String field1, LocalDate field2, Integer field3)
		{
			super(field1, field2, field3);
		}

		public static Builder builder() {return new Builder();}

		public static class Builder
		{
			private String field1;
			private LocalDate field2;
			private Integer field3;

			public Builder field1(String value)
			{
				this.field1 = value;
				return this;
			}

			public Builder field2(LocalDate value)
			{
				this.field2 = value;
				return this;
			}

			public Builder field3(Integer value)
			{
				this.field3 = value;
				return this;
			}

			public BuilderMock build() {return new BuilderMock(field1, field2, field3);}
		}
	}

	public static class AmbiguousConstructorMock extends ConstructionMock
	{
		public AmbiguousConstructorMock(String field1, LocalDate field2, Integer field3)
		{
			super(field1, field2, field3);
		}

		public static AmbiguousConstructorMock of(String field1, LocalDate field2, Integer field3)
		{
			return new AmbiguousConstructorMock(field1, field2, field3);
		}
	}

	public static final class CanonicalConstructorMock extends AmbiguousConstructorMock
	{
		@Canonical
		public CanonicalConstructorMock(String field1, LocalDate field2, Integer field3)
		{
			super(field1, field2, field3);
		}

		public static CanonicalConstructorMock of(String field1, LocalDate field2, Integer field3)
		{
			return new CanonicalConstructorMock(field1, field2, field3);
		}
	}

	public static final class SingleConstructorMock extends ConstructionMock
	{
		public SingleConstructorMock(String field1, LocalDate field2, Integer field3)
		{
			super(field1, field2, field3);
		}

		@Deprecated
		public SingleConstructorMock(String field1)
		{
			this(field1, null, null);
		}
	}

	public static final class SingleFactoryMock extends ConstructionMock
	{

		private SingleFactoryMock(String field1, LocalDate field2, Integer field3)
		{
			super(field1, field2, field3);
		}

		public static SingleFactoryMock of(String field1, LocalDate field2, Integer field3) {return new SingleFactoryMock(field1, field2, field3);}

	}

	public static final class MultipleConstructorMock extends ConstructionMock
	{
		public MultipleConstructorMock(String field1)
		{
			super(field1, null, null);
		}

		public MultipleConstructorMock(String field1, LocalDate field2)
		{
			super(field1, field2, null);
		}

		public MultipleConstructorMock(String field1, LocalDate field2, Integer field3)
		{
			super(field1, field2, field3);
		}
	}

	public static final class PrimitiveFactoryMock
	{
		private final String field1;
		private final int field2;

		private PrimitiveFactoryMock(String field1, int field2)
		{
			this.field1 = field1;
			this.field2 = field2;
		}

		public String getField1() {return field1;}

		public int getField2() {return field2;}

		public static PrimitiveFactoryMock of(String field1, int field2)
		{
			return new PrimitiveFactoryMock(field1, field2);
		}
	}

	public static final class PrimitiveConstructorMock
	{
		private final String field1;
		private final int field2;

		public PrimitiveConstructorMock(String field1, int field2)
		{
			this.field1 = field1;
			this.field2 = field2;
		}

		public String getField1() {return field1;}

		public int getField2() {return field2;}
	}


}