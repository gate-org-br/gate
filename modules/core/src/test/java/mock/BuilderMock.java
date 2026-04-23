package mock;

public class BuilderMock
{
	private final String field1;
	private final String field2;
	private final String field3;

	private BuilderMock(String field1, String field2, String field3)
	{
		this.field1 = field1;
		this.field2 = field2;
		this.field3 = field3;
	}

	public String getField1() {return field1;}

	public String getField2() {return field2;}

	public String getField3() {return field3;}

	public static Builder builder() {return new Builder();}

	public static class Builder
	{
		private String field1;
		private String field2;
		private String field3;

		public Builder field1(String value)
		{
			this.field1 = value;
			return this;
		}

		public Builder field2(String value)
		{
			this.field2 = value;
			return this;
		}

		public Builder field3(String value)
		{
			this.field3 = value;
			return this;
		}

		public BuilderMock build() {return new BuilderMock(field1, field2, field3);}
	}
}