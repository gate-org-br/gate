package gate.type;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Category implements Comparable<Category>
{

	private Number value;
	private Comparable name;
	private static final long serialVersionUID = 1L;
	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	public Category(Comparable name, Number value)
	{
		if (name == null)
			name = "Indefinido";
		if (value == null)
			throw new NullPointerException("value");

		this.name = name;
		this.value = value;
	}

	public Comparable getName()
	{
		return name;
	}

	public Number getValue()
	{
		return value;
	}

	public BigDecimal getPercentage(BigDecimal total)
	{
		if (BigDecimal.ZERO.compareTo(total) == 0)
			return BigDecimal.ZERO;
		return new BigDecimal(this.getValue().doubleValue()).divide(total, 4, RoundingMode.DOWN).multiply(ONE_HUNDRED);
	}

	@Override
	public int compareTo(Category category)
	{
		return ((Comparable) category.getValue()).compareTo((Comparable) getValue());
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Category
				&& name.equals(((Category) obj).name)
				&& value.equals(((Category) obj).value);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.name) + Objects.hashCode(this.value);
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}
