package gate.type;

import gate.lang.json.JsonArray;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PivotTable<T> implements Serializable
{

	private final String rowLabel;
	private final String colLabel;
	private final String valueLabel;
	private final T defaultValue;

	private static final long serialVersionUID = 1L;
	private final Set<String> columns = new LinkedHashSet<>();
	private final Map<String, Map<String, T>> values = new LinkedHashMap<>();

	public PivotTable(String rowLabel, String colLabel, String valueLabel, T defaultValue)
	{
		this.rowLabel = rowLabel;
		this.colLabel = colLabel;
		this.valueLabel = valueLabel;
		this.defaultValue = defaultValue;
	}

	public PivotTable add(String row, String col, T value)
	{
		columns.add(col);
		values.computeIfAbsent(row, e -> new LinkedHashMap<>()).put(col, value);
		return this;
	}

	public T get(String row, String col)
	{
		Map<String, T> value = values.get(row);
		return value != null ? value.getOrDefault(col, defaultValue) : defaultValue;
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
		return Stream.concat(Stream.of(getRowLabel()), columns.stream()).toList();
	}

	public List<List<Object>> values()
	{
		return values.entrySet().stream()
			.map(row -> Stream.concat(Stream.of(row.getKey()),
			columns.stream().map(e -> row.getValue().getOrDefault(e, defaultValue)))
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

	public PivotTable inverted()
	{
		PivotTable result = new PivotTable(getColLabel(), getRowLabel(), getValueLabel(), defaultValue);

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
