package gate.type;

import gate.error.HierarchyException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TreeNodeTest
{

	@Test
	public void testSetParent()
	{
		var root = new Node("root");
		var branch = new Node("branch");
		var leaf = new Node("leaf");

		branch.setParent(root);
		leaf.setParent(branch);

		assertSame(root, branch.getParent());
		assertSame(branch, leaf.getParent());
		assertEquals(List.of(branch), root.getChildren());
		assertEquals(List.of(leaf), branch.getChildren());
		assertEquals(List.of(branch, leaf), root.descendants().toList());
		assertEquals(List.of(root, branch, leaf), leaf.path().toList());
		assertEquals(List.of(branch, root), leaf.ancestors().toList());
		assertSame(root, leaf.root());
		assertEquals(2, leaf.depth());
		assertTrue(root.isRoot());
		assertFalse(root.isLeaf());
		assertTrue(leaf.isLeaf());
	}

	@Test
	public void testSetChildren()
	{
		var root = new Node("root");
		var first = new Node("first");
		var second = new Node("second");
		var removed = new Node("removed");

		root.setChildren(List.of(first, removed));
		root.setChildren(List.of(first, second));

		assertEquals(List.of(first, second), root.getChildren());
		assertSame(root, first.getParent());
		assertSame(root, second.getParent());
		assertNull(removed.getParent());
		assertEquals(List.of(first, second), root.descendants().toList());
	}

	@Test
	public void testReparentSubtree()
	{
		var source = new Node("source");
		var target = new Node("target");
		var branch = new Node("branch");
		var leaf = new Node("leaf");

		source.setChildren(List.of(branch));
		branch.setChildren(List.of(leaf));

		branch.setParent(target);

		assertEquals(List.of(), source.getChildren());
		assertEquals(List.of(branch), target.getChildren());
		assertSame(target, branch.getParent());
		assertSame(target, branch.root());
		assertSame(target, leaf.root());
		assertTrue(target.contains(leaf));
		assertFalse(source.contains(branch));
		assertFalse(source.contains(leaf));
	}

	@Test
	public void testRelationships()
	{
		var root = new Node("root");
		var first = new Node("first");
		var second = new Node("second");
		var leaf = new Node("leaf");

		root.setChildren(List.of(first, second));
		first.setChildren(List.of(leaf));

		assertTrue(root.contains(root));
		assertTrue(root.contains(first));
		assertTrue(root.contains(leaf));
		assertFalse(first.contains(second));
		assertTrue(leaf.isInSubtreeOf(root));
		assertTrue(leaf.isInSubtreeOf(first));
		assertTrue(leaf.isInSubtreeOf(leaf));
		assertFalse(root.isInSubtreeOf(leaf));
		assertTrue(leaf.isDescendantOf(root));
		assertTrue(root.isAncestorOf(leaf));
		assertEquals(List.of(second), first.siblings().toList());
		assertEquals(List.of(first), second.siblings().toList());
		assertEquals(List.of(leaf, second), root.leaves().toList());
	}

	@Test
	public void testCircularRelationshipsAreRejected()
	{
		var root = new Node("root");
		var branch = new Node("branch");
		var leaf = new Node("leaf");

		root.setChildren(List.of(branch));
		branch.setChildren(List.of(leaf));

		assertThrows(HierarchyException.class, () -> root.setParent(leaf));
		assertThrows(HierarchyException.class, () -> leaf.setChildren(List.of(root)));
	}

	@Test
	public void testNullChildrenListIsRejected()
	{
		var root = new Node("root");
		var child = new Node("child");

		assertThrows(HierarchyException.class, () -> root.setChildren(null));
		assertThrows(HierarchyException.class, () -> root.setChildren(Arrays.asList(child, null)));
	}

	@Test
	public void testDuplicateChildrenAreRejected()
	{
		var root = new Node("root");
		var child = new Node("child");

		assertThrows(HierarchyException.class, () -> root.setChildren(List.of(child, child)));
	}

	@Test
	public void testDifferentEqualObjectsAreDifferentNodes()
	{
		var root = new EqualNode("root");
		var first = new EqualNode("child");
		var second = new EqualNode("child");

		root.setChildren(List.of(first, second));

		assertEquals(2, root.getChildren().size());
		assertSame(first, root.getChildren().get(0));
		assertSame(second, root.getChildren().get(1));
		assertTrue(root.contains(first));
		assertTrue(root.contains(second));
		assertTrue(first.siblings().anyMatch(e -> e == second));
		assertTrue(second.siblings().anyMatch(e -> e == first));

		root.setChildren(List.of(first));

		assertEquals(1, root.getChildren().size());
		assertSame(first, root.getChildren().get(0));
		assertSame(root, first.getParent());
		assertNull(second.getParent());
		assertFalse(root.contains(second));
	}

	@Test
	public void testConstraintsAreApplied()
	{
		var parent = new Node("parent");
		var accepted = new Node("accepted");
		var rejected = new Node("rejected", Set.of((newParent, node) ->
		{
			throw new HierarchyException("Rejected");
		}));

		accepted.setParent(parent);

		assertSame(parent, accepted.getParent());
		assertThrows(HierarchyException.class, () -> rejected.setParent(parent));
		assertNull(rejected.getParent());
		assertEquals(List.of(accepted), parent.getChildren());
	}

	private static class Node extends TreeNode<Node>
	{

		private final String name;
		private final Set<BiConsumer<Node, Node>> constraints;

		private Node(String name)
		{
			this(name, Set.of());
		}

		private Node(String name, Set<BiConsumer<Node, Node>> constraints)
		{
			this.name = name;
			this.constraints = new LinkedHashSet<>(constraints);
		}

		@Override
		public Set<BiConsumer<Node, Node>> constraints()
		{
			return constraints;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	private static class EqualNode extends TreeNode<EqualNode>
	{

		private final String name;

		private EqualNode(String name)
		{
			this.name = name;
		}

		@Override
		public boolean equals(Object object)
		{
			return object instanceof EqualNode node
					&& Objects.equals(name, node.name);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(name);
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}
