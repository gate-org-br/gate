package gate.type;

import gate.error.HierarchyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Node belonging to an in-memory hierarchical structure.
 *
 * <p>
 * This class prioritizes structural consistency over performance for large
 * hierarchies. It keeps parent and children relationships coherent, rejects
 * circular relationships, and applies constraints before structural changes.
 * </p>
 *
 * <p>
 * Nodes are compared by object identity, not by {@link Object#equals(Object)}.
 * </p>
 *
 * @param <T> concrete node type
 */
@SuppressWarnings("unchecked")
public abstract class TreeNode<T extends TreeNode<T>> implements Hierarchical<T>
{
	private T parent;
	private List<T> tree = new ArrayList<>(List.of((T) this));

	/**
	 * Returns the constraints that must be satisfied before structural
	 * modifications are applied.
	 *
	 * <p>
	 * The first argument is the proposed parent and the second argument
	 * is the node being modified.
	 * </p>
	 *
	 * @return constraints associated with this node
	 */
	public Set<BiConsumer<T, T>> constraints()
	{
		return Set.of();
	}

	/**
	 * Returns the direct parent of this node.
	 *
	 * @return parent or {@code null} if this node is the root
	 */
	public T getParent()
	{
		return parent;
	}

	/**
	 * Changes the parent of this node.
	 *
	 * @param parent new parent or {@code null}
	 * @return this node
	 * @throws HierarchyException if the operation would create a circular relationship
	 *                            or violate a constraint
	 */
	@Override
	public final T setParent(T parent)
	{
		if (parent == this.parent)
			return (T) this;

		if (contains(parent))
			throw new HierarchyException("Attempt to create a circular relationship");
		constraints().forEach(c -> c.accept(parent, (T) this));
		if (parent != null)
			parent.constraints().forEach(c -> c.accept(parent, (T) this));
		updateParent(parent);
		return (T) this;
	}

	/**
	 * Returns the direct children of this node.
	 *
	 * @return direct children
	 */
	public List<T> getChildren()
	{
		return tree.stream()
				.filter(e -> ((TreeNode<T>) e).parent == this)
				.toList();
	}

	/**
	 * Replaces the direct children of this node.
	 *
	 * @param children new direct children
	 * @return this node
	 * @throws HierarchyException if {@code children} is {@code null}, contains
	 *                            {@code null}, contains duplicate nodes, the operation
	 *                            would create a circular relationship, or a constraint
	 *                            is violated
	 */
	@Override
	public final T setChildren(List<T> children)
	{
		if (children == null)
			throw new HierarchyException("Attempt to insert a null children list on a tree node");
		if (children.stream().anyMatch(Objects::isNull))
			throw new HierarchyException("Attempt to insert a null child on a tree node");
		if (children.stream().anyMatch(this::isInSubtreeOf))
			throw new HierarchyException("Attempt to create a circular relationship");
		for (int i = 0; i < children.size(); i++)
			for (int y = i + 1; y < children.size(); y++)
				if (children.get(i) == children.get(y))
					throw new HierarchyException("Attempt to insert duplicate children on a tree node");

		var include = children.stream().filter(e -> ((TreeNode<T>) e).parent != this).toList();
		var exclude = getChildren().stream()
				.filter(e -> children.stream().noneMatch(child -> child == e))
				.toList();
		include.forEach(e -> constraints().forEach(c -> c.accept((T) this, e)));
		include.forEach(e -> e.constraints().forEach(c -> c.accept((T) this, e)));
		exclude.forEach(e -> e.constraints().forEach(c -> c.accept(null, e)));

		include.forEach(e -> e.updateParent((T) this));
		exclude.forEach(e -> e.updateParent(null));
		return (T) this;
	}

	/**
	 * Returns whether this node is the supplied node or one of its descendants.
	 *
	 * @param node node to test
	 * @return {@code true} if this node belongs to the supplied node's subtree
	 */
	public boolean isInSubtreeOf(T node)
	{
		return this == node || isDescendantOf(node);
	}

	/**
	 * Returns whether this node is a descendant of the supplied node.
	 *
	 * @param node potential ancestor
	 * @return {@code true} if this node is contained in the supplied node's subtree
	 */
	public boolean isDescendantOf(T node)
	{
		return node != null
				&& (((TreeNode<T>) node)).tree == this.tree
				&& ancestors().anyMatch(e -> e == node);
	}

	/**
	 * Returns whether the supplied node belongs to this subtree.
	 *
	 * @param node node to test
	 * @return {@code true} if the node belongs to this subtree
	 */
	public boolean contains(T node)
	{
		return node != null
				&& (((TreeNode<T>) node)).tree == this.tree
				&& subtree().anyMatch(e -> e == node);
	}

	/**
	 * Returns the siblings of this node.
	 *
	 * @return nodes sharing the same parent, excluding this node
	 */
	public Stream<T> siblings()
	{
		return parent == null
				? Stream.empty()
				: parent.getChildren().stream().filter(e -> e != this);
	}

	void updateParent(T parent)
	{
		if (parent == null || (((TreeNode<T>) parent)).tree != this.tree)
		{
			List<T> target = parent != null
					? (((TreeNode<T>) parent)).tree
					: new ArrayList<>();

			this.subtree().toList().forEach(e ->
			{
				(((TreeNode<T>) e)).tree.removeIf(node -> node == e);
				target.add(e);
				((TreeNode<T>) e).tree = target;
			});
		}
		this.parent = parent;
	}
}