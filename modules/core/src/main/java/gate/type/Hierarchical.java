package gate.type;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Element that exposes direct children in a hierarchical structure.
 *
 * @param <T> concrete element type
 */
@SuppressWarnings("unchecked")
public interface Hierarchical<T extends Hierarchical<T>>
{

	/**
	 * Returns the direct parent of this element.
	 *
	 * @return parent or {@code null} if this element is the root
	 */
	T getParent();

	/**
	 * Changes the parent of this element.
	 *
	 * @param parent new parent or {@code null}
	 * @return this element
	 */
	T setParent(T parent);

	/**
	 * Returns the direct children of this element.
	 *
	 * @return direct children
	 */
	List<T> getChildren();

	/**
	 * Replaces the direct children of this element.
	 *
	 * @param children new direct children
	 * @return this element
	 */
	T setChildren(List<T> children);

	/**
	 * Returns the root element of the hierarchy containing this element.
	 *
	 * @return root element
	 */
	default T root()
	{
		return getParent() == null ? (T) this : getParent().root();
	}

	/**
	 * Returns whether this element is the root of its hierarchy.
	 *
	 * @return {@code true} if this element has no parent
	 */
	default boolean isRoot()
	{
		return getParent() == null;
	}

	/**
	 * Returns whether this element has no children.
	 *
	 * @return {@code true} if this element has no children
	 */
	default boolean isLeaf()
	{
		return getChildren().isEmpty();
	}

	/**
	 * Returns whether this element is the supplied element or one of its descendants.
	 *
	 * @param element element to test
	 * @return {@code true} if this element belongs to the supplied element's subtree
	 */
	default boolean isInSubtreeOf(T element)
	{
		return equals(element) || isDescendantOf(element);
	}

	/**
	 * Returns whether this element is a descendant of the supplied element.
	 *
	 * @param element potential ancestor
	 * @return {@code true} if this element is contained in the supplied element's subtree
	 */
	default boolean isDescendantOf(T element)
	{
		return element != null && ancestors().anyMatch(e -> e.equals(element));
	}

	/**
	 * Returns whether the supplied element is a descendant of this element.
	 *
	 * @param element potential descendant
	 * @return {@code true} if the supplied element is contained in this subtree
	 */
	default boolean isAncestorOf(T element)
	{
		return element != null && element.isDescendantOf((T) this);
	}

	/**
	 * Returns whether the supplied element belongs to this subtree.
	 *
	 * @param element element to test
	 * @return {@code true} if the element belongs to this subtree
	 */
	default boolean contains(T element)
	{
		return element != null && subtree().anyMatch(e -> e.equals(element));
	}

	/**
	 * Returns the direct parent and its ancestors up to the root.
	 *
	 * @return parent, grandparent and so on up to the root
	 */
	default Stream<T> ancestors()
	{
		return getParent() == null
				? Stream.empty()
				: Stream.concat(Stream.of(getParent()), getParent().ancestors());
	}

	/**
	 * Returns all descendants of this element.
	 *
	 * @return descendants excluding this element
	 */
	default Stream<T> descendants()
	{
		return getChildren().stream()
				.flatMap(child -> Stream.concat(Stream.of(child), child.descendants()));
	}

	/**
	 * Returns this element and all its descendants.
	 *
	 * @return subtree rooted at this element
	 */
	default Stream<T> subtree()
	{
		return Stream.concat(Stream.of((T) this), descendants());
	}

	/**
	 * Returns the path from the root element to this element.
	 *
	 * @return path from the root to this element
	 */
	default Stream<T> path()
	{
		return getParent() == null
				? Stream.of((T) this)
				: Stream.concat(getParent().path(), Stream.of((T) this));
	}

	/**
	 * Returns this element and its ancestors up to the root.
	 *
	 * @return this element, parent, grandparent and so on up to the root
	 */
	default Stream<T> lineage()
	{
		return Stream.concat(Stream.of((T) this), ancestors());
	}

	/**
	 * Returns the siblings of this element.
	 *
	 * @return elements sharing the same parent, excluding this element
	 */
	default Stream<T> siblings()
	{
		return getParent() == null
				? Stream.empty()
				: getParent().getChildren().stream().filter(e -> !e.equals(this));
	}

	/**
	 * Returns the depth of this element in the hierarchy.
	 *
	 * @return number of ancestors, zero if this element is the root
	 */
	default int depth()
	{
		return (int) ancestors().count();
	}

	/**
	 * Returns all leaf elements in this subtree.
	 *
	 * @return descendants with no children, excluding this element unless it is a leaf
	 */
	default Stream<T> leaves()
	{
		return subtree().filter(Hierarchical::isLeaf);
	}

	/**
	 * Returns this element and all its descendants as a list.
	 *
	 * @return subtree rooted at this element
	 */
	default List<T> toList()
	{
		return subtree().toList();
	}

	/**
	 * Returns values extracted from this element and all its descendants.
	 *
	 * @param <E>       extracted value type
	 * @param extractor function used to extract a value from each element
	 * @return extracted values from this subtree
	 */
	default <E> List<E> toList(Function<T, E> extractor)
	{
		return subtree().map(extractor).toList();
	}
}
