package gate.type;

import gate.error.AppException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class HierarchyTest
{

	public HierarchyTest()
	{
	}

	@Test
	public void testValid() throws AppException
	{
		Mock mock1 = new Mock(new ID(1));

		Mock mock11 = new Mock(new ID(11)).setParent(mock1);
		Mock mock111 = new Mock(new ID(111)).setParent(mock11);
		Mock mock112 = new Mock(new ID(112)).setParent(mock11);
		Mock mock113 = new Mock(new ID(113)).setParent(mock11);

		Mock mock12 = new Mock(new ID(12)).setParent(mock1);
		Mock mock13 = new Mock(new ID(13)).setParent(mock1);

		Mock mock2 = new Mock(new ID(2));
		Mock mock21 = new Mock(new ID(21)).setParent(mock2);
		Mock mock22 = new Mock(new ID(22)).setParent(mock2);
		Mock mock23 = new Mock(new ID(23)).setParent(mock2);

		Hierarchy.setup(Arrays.asList(mock1, mock11, mock111, mock112, mock113, mock12, mock13, mock2, mock21, mock22, mock23));
		mock1.getChildren().equals(Arrays.asList(mock11, mock12, mock13));
		mock2.getChildren().equals(Arrays.asList(mock21, mock22, mock23));
		mock1.select(new ID(111)).equals(mock111);
	}

	@Test
	public void testInvalid()
	{
		try
		{
			Mock mock1 = new Mock(new ID(1));
			Mock mock11 = new Mock(new ID(11)).setParent(mock1);
			Mock mock12 = new Mock(new ID(12)).setParent(mock1);
			Mock mock13 = new Mock(new ID(13)).setParent(mock1);

			mock1.setParent(mock11);

			Mock mock2 = new Mock(new ID(2));
			Mock mock21 = new Mock(new ID(21)).setParent(mock2);
			Mock mock22 = new Mock(new ID(22)).setParent(mock2);
			Mock mock23 = new Mock(new ID(23)).setParent(mock2);

			mock2.setParent(mock21);

			Hierarchy.setup(Arrays.asList(mock1, mock11, mock12, mock13, mock2, mock21, mock22, mock23));
			Assert.fail();
		} catch (AppException ex)
		{
		}
	}

	private class Mock implements Hierarchy<Mock>
	{

		private final ID id;
		private Mock parent;
		private List<Mock> children;

		public Mock(ID id)
		{
			this.id = id;
		}

		@Override
		public List<Mock> getChildren()
		{
			if (children == null)
				children = new ArrayList<>();
			return children;
		}

		@Override
		public ID getId()
		{
			return id;
		}

		@Override
		public Mock getParent()
		{
			if (parent == null)
				parent = new Mock(null);
			return parent;
		}

		@Override
		public Mock setChildren(List<Mock> children)
		{
			this.children = children;
			return this;
		}

		@Override
		public Mock setParent(Mock parent)
		{
			this.parent = parent;
			return this;
		}

		@Override
		public String toString()
		{
			return id.toString();
		}

	}
}
