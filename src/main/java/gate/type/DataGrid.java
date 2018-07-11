package gate.type;

import gate.converter.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataGrid extends ArrayList<Object[]>
{

	private static final long serialVersionUID = 1L;

	public String[] head;
	public Object[] foot;

	public DataGrid(String[] head)
	{
		this.head = head;
	}

	public DataGrid(String[] head, Object[] foot)
	{
		this.head = head;
		this.foot = foot;
	}

	public void setHead(String[] head)
	{
		this.head = head;
	}

	public void setFoot(Object[] foot)
	{
		this.foot = foot;
	}

	public String[] getHead()
	{
		return head;
	}

	public Object[] getFoot()
	{
		return foot;
	}

	public String toString(Number... indexes)
	{
		return this.toString(Arrays.asList(indexes));
	}

	public String toString(List<Number> indexes)
	{
		StringBuilder string = new StringBuilder();

		StringBuilder head = new StringBuilder();
		for (int i = 0; i < indexes.size(); i++)
		{
			Object value = getHead()[indexes.get(i).intValue()];
			if (head.length() > 0)
				head.append(", ");
			if (!(value instanceof Number || value instanceof Boolean))
				head.append("\"");
			head.append(value != null ? value.toString() : "");
			if (!(value instanceof Number || value instanceof Boolean))
				head.append("\"");
		}
		string.append("[").append(head.toString()).append("]");

		for (Object[] values : this)
		{
			StringBuilder line = new StringBuilder();
			for (int i = 0; i < indexes.size(); i++)
			{
				Object value = values[indexes.get(i).intValue()];
				if (line.length() > 0)
					line.append(", ");
				if (!(value instanceof Number || value instanceof Boolean))
					line.append("\"");

				if (i > 0)
					value = Converter.toNumber(value);
				line.append(value != null ? value.toString() : "");
				if (!(value instanceof Number || value instanceof Boolean))
					line.append("\"");
			}
			string.append(", ").append("[").append(line.toString()).append("]");
		}
		return String.format("[%s]", string.toString());
	}

	@Override
	public String toString()
	{
		Number[] indexes = new Number[head.length];
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = i;
		return toString(indexes);
	}

	public DataGrid rollup()
	{
		if (size() > 0)
			setFoot(remove(size() - 1));
		return this;
	}
}
