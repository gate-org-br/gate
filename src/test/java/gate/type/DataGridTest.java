package gate.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DataGridTest
{

	public DataGridTest()
	{
	}

	@Test
	public void testToString()
	{
		DataGrid dataGrid = new DataGrid("Column 1", "Column 2");
		dataGrid.insert(1, 2);
		dataGrid.insert(3, 4);
		dataGrid.insert(5, 6);
		assertEquals("[ [ \"Column 1\",\"Column 2\" ],[ 1,2 ],[ 3,4 ],[ 5,6 ] ]", dataGrid.toString());
	}

	@Test
	public void testIndexedToString()
	{
		DataGrid dataGrid = new DataGrid("Column 0", "Column 1", "Column 2", "Column 3");
		dataGrid.insert(3, 1, 2, 3);
		dataGrid.insert(2, 3, 4, 5);
		dataGrid.insert(1, 5, 6, 7);
		assertEquals("[ [ \"Column 1\",\"Column 2\" ],[ 1,2 ],[ 3,4 ],[ 5,6 ] ]", dataGrid.toString(1, 2));
	}
}
