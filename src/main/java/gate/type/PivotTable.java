package gate.type;

import gate.lang.json.JsonArray;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PivotTable<T> implements Serializable
{

	private final String rowLabel;
	private final String colLabel;
	private final String valueLabel;

	private static final long serialVersionUID = 1L;
	private final Map<String, Map<String, T>> values = new LinkedHashMap<>();

	public PivotTable(String rowLabel, String colLabel, String valueLabel)
	{
		this.rowLabel = rowLabel;
		this.colLabel = colLabel;
		this.valueLabel = valueLabel;
	}

	public PivotTable<T> add(String row, String col, T value)
	{
		values.computeIfAbsent(row, e -> new LinkedHashMap<>()).put(col, value);
		return this;
	}

	public T get(String row, String col)
	{
		Map<String, T> value = values.get(row);
		return value != null ? value.get(col) : null;
	}

	public T remove(String row, String col)
	{
		Map<String, T> value = values.get(row);
		if (value != null)
		{
			T result = value.remove(col);
			if (value.isEmpty())
				values.remove(row);
			return result;
		}

		return null;
	}

	public List<String> header()
	{
		return Stream
				.concat(Stream.of(getRowLabel()), values.values().stream()
						.flatMap(e -> e.keySet().stream()).map(e -> String.valueOf(e)))
				.distinct().toList();
	}

	public List<List<Object>> values()
	{
		return values.entrySet().stream()
				.map(row -> Stream.concat(Stream.of(row.getKey()), row.getValue().values().stream())
						.collect(Collectors.toList()))
				.collect(Collectors.toList());
	}

	public String getRowLabel()
	{
		return rowLabel;
	}

	public String getColLabel()
	{
		return colLabel;
	}

	public String getValueLabel()
	{
		return valueLabel;
	}

	public List<Object> dataset()
	{
		return Stream.concat(Stream.of(header()), values().stream()).collect(Collectors.toList());
	}

	public boolean isEmpty()
	{
		return values.isEmpty();
	}

	public JsonArray toJson()
	{
		return JsonArray.of(dataset());
	}

	public PivotTable<T> inverted()
	{
		PivotTable<T> result = new PivotTable<T>(getColLabel(), getRowLabel(), getValueLabel());

		for (Map.Entry<String, Map<String, T>> row : values.entrySet())
			for (Map.Entry<String, T> col : row.getValue().entrySet())
				result.add(col.getKey(), row.getKey(), col.getValue());

		return result;
	}

	@Override
	public String toString()
	{
		return toJson().toString();
	}
}
