package gate.adapter.jsonConverter;

import gate.type.DataGrid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataGridJsonConverterTest
{
	@Test
	public void testShouldConvertToJsonAndBack()
	{
		var grid = new DataGrid("col1", "col2");
		grid.insert("a", 1);
		var json = JsonConverter.toJson(grid);
		var value = (DataGrid) JsonConverter.fromJson(DataGrid.class, json);
		Assertions.assertEquals(json, JsonConverter.toJson(value));
	}
}
