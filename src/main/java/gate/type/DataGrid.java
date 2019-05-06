package gate.type;

import gate.converter.Converter;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static javassist.CtMethod.ConstParameter.string;

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
		JsonArray result = new JsonArray();
		result.add(indexes.stream()
			.map(e -> e.intValue())
			.map(e -> getHead()[e])
			.map(e -> new JsonString(e))
			.collect(Collectors.toCollection(() -> new JsonArray())));

		stream().map(values
			-> indexes.stream()
				.map(e -> e.intValue())
				.map(e -> values[e])
				.map(e -> JsonElement.valueOf(e))
				.collect(Collectors.toCollection(() -> new JsonArray())))
			.forEach(result::add);

		return result.toString();
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
