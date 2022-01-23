package gate.type;

import org.junit.Assert;
import org.junit.Test;

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
		Assert.assertEquals("[ [ \"Column 1\",\"Column 2\" ],[ 1,2 ],[ 3,4 ],[ 5,6 ] ]", dataGrid.toString());
	}

	@Test
	public void testIndexedToString()
	{
		DataGrid dataGrid = new DataGrid("Column 0", "Column 1", "Column 2", "Column 3");
		dataGrid.insert(3, 1, 2, 3);
		dataGrid.insert(2, 3, 4, 5);
		dataGrid.insert(1, 5, 6, 7);
		Assert.assertEquals("[ [ \"Column 1\",\"Column 2\" ],[ 1,2 ],[ 3,4 ],[ 5,6 ] ]", dataGrid.toString(1, 2));
	}
}
