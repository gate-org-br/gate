package gate.type;

import gate.error.AppException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class HierarchyTest
{

	private static final Mock MOCK1 = new Mock(ID.valueOf(1));

	private static final Mock MOCK11 = new Mock(ID.valueOf(11)).setParent(MOCK1);
	private static final Mock MOCK111 = new Mock(ID.valueOf(111)).setParent(MOCK11);
	private static final Mock MOCK112 = new Mock(ID.valueOf(112)).setParent(MOCK11);
	private static final Mock MOCK113 = new Mock(ID.valueOf(113)).setParent(MOCK11);

	private static final Mock MOCK12 = new Mock(ID.valueOf(12)).setParent(MOCK1);
	private static final Mock MOCK121 = new Mock(ID.valueOf(122)).setParent(MOCK12);
	private static final Mock MOCK122 = new Mock(ID.valueOf(122)).setParent(MOCK12);
	private static final Mock MOCK123 = new Mock(ID.valueOf(123)).setParent(MOCK12);

	private static final Mock MOCK13 = new Mock(ID.valueOf(13)).setParent(MOCK1);
	private static final Mock MOCK131 = new Mock(ID.valueOf(131)).setParent(MOCK13);
	private static final Mock MOCK132 = new Mock(ID.valueOf(132)).setParent(MOCK13);
	private static final Mock MOCK133 = new Mock(ID.valueOf(133)).setParent(MOCK13);

	private static final Mock MOCK2 = new Mock(ID.valueOf(2));

	private static final Mock MOCK21 = new Mock(ID.valueOf(21)).setParent(MOCK2);
	private static final Mock MOCK211 = new Mock(ID.valueOf(211)).setParent(MOCK21);
	private static final Mock MOCK212 = new Mock(ID.valueOf(212)).setParent(MOCK21);
	private static final Mock MOCK213 = new Mock(ID.valueOf(213)).setParent(MOCK21);

	private static final Mock MOCK22 = new Mock(ID.valueOf(22)).setParent(MOCK2);
	private static final Mock MOCK221 = new Mock(ID.valueOf(221)).setParent(MOCK22);
	private static final Mock MOCK222 = new Mock(ID.valueOf(222)).setParent(MOCK22);
	private static final Mock MOCK223 = new Mock(ID.valueOf(223)).setParent(MOCK22);

	private static final Mock MOCK23 = new Mock(ID.valueOf(23)).setParent(MOCK2);
	private static final Mock MOCK231 = new Mock(ID.valueOf(231)).setParent(MOCK23);
	private static final Mock MOCK232 = new Mock(ID.valueOf(232)).setParent(MOCK23);
	private static final Mock MOCK233 = new Mock(ID.valueOf(233)).setParent(MOCK23);

	private final List<Mock> MOCKS = Arrays.asList(
		MOCK1,
		MOCK11, MOCK111, MOCK112, MOCK113,
		MOCK12, MOCK121, MOCK122, MOCK123,
		MOCK13, MOCK131, MOCK132, MOCK133,
		MOCK2,
		MOCK21, MOCK211, MOCK212, MOCK213,
		MOCK22, MOCK221, MOCK222, MOCK223,
		MOCK23, MOCK231, MOCK232, MOCK233);

	@Test
	public void testValid() throws AppException
	{
		Hierarchy.setup(MOCKS);
		MOCK1.getChildren().equals(Arrays.asList(MOCK11, MOCK12, MOCK13));
		MOCK2.getChildren().equals(Arrays.asList(MOCK21, MOCK22, MOCK23));
		MOCK1.select(ID.valueOf(111)).equals(MOCK111);
	}

	@Test
	public void testToList() throws AppException
	{
		Hierarchy.setup(MOCKS);
		assertEquals(Arrays.asList(MOCK1,
			MOCK11, MOCK111, MOCK112, MOCK113, MOCK12, MOCK121, MOCK122, MOCK123,
			MOCK13, MOCK131, MOCK132, MOCK133), MOCK1.toList());
	}

	@Test
	public void testToParentList() throws AppException
	{
		Hierarchy.setup(MOCKS);
		assertEquals(Arrays.asList(MOCK121, MOCK12, MOCK1), MOCK121.toParentList());
	}

	@Test
	public void testSelect() throws AppException
	{
		Hierarchy.setup(MOCKS);
		MOCK123.getRoot().select(MOCK111.getId())
			.equals(MOCK111);
	}

	private static class Mock implements Hierarchy<Mock>
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
