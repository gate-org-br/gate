package gate.type;

import gate.error.HierarchyException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an entity that can be organized in a tree hierarchy.
 * <p>
 * Each element may have a parent and a list of children. The root element is
 * the one whose parent is null or has a null id. Use {@link #setup(List)} to
 * assemble a flat list of elements into a proper hierarchy, validating for
 * duplicates and circular references in the process.
 *
 * @param <T> the concrete type implementing this interface
 */
public interface Hierarchy<T extends Hierarchy<T>> extends Hierarchical<T>
{

	/**
	 * Returns the unique identifier of this element.
	 *
	 * @return the id of this element
	 */
	ID getId();

	/**
	 * Returns the parent of this element, or an empty reference if this is the root.
	 *
	 * @return the parent of this element
	 */
	T getParent();

	/**
	 * Returns the direct children of this element.
	 *
	 * @return the list of direct children
	 */
	List<T> getChildren();

	/**
	 * Sets the parent of this element.
	 *
	 * @param parent the parent to set
	 * @return this element
	 */
	T setParent(T parent);

	/**
	 * Sets the direct children of this element.
	 *
	 * @param children the children to set
	 * @return this element
	 */
	T setChildren(List<T> children);

	/**
	 * Checks if this element is an ancestor of the specified element.
	 *
	 * @param entity the element to check
	 * @return true if this element is a direct or indirect parent of the specified element
	 * @throws NullPointerException if the specified element is null or has a null id
	 * @deprecated use {@link #isAncestorOf(Hierarchy)}
	 */
	@Deprecated(since = "21.0.0", forRemoval = true)
	default boolean isParentOf(T entity)
	{
		return isAncestorOf(entity);
	}

	/**
	 * Checks if this element is an ancestor of the specified element.
	 *
	 * @param entity the element to check
	 * @return true if this element is a direct or indirect parent of the specified element
	 * @throws NullPointerException if the specified element is null or has a null id
	 */
	@Override
	default boolean isAncestorOf(T entity)
	{
		Objects.requireNonNull(entity);
		Objects.requireNonNull(entity.getId());
		return getChildren().stream().anyMatch(e -> e.equals(entity) || e.isAncestorOf(entity));
	}

	/**
	 * Returns the root of the hierarchy this element belongs to.
	 *
	 * @return the root element, which may be this element itself if it has no parent
	 * @deprecated use {@link #root()}
	 */
	@Deprecated(since = "21.0.0", forRemoval = true)
	@SuppressWarnings("unchecked")
	default T getRoot()
	{
		return root();
	}

	/**
	 * Returns the root of the hierarchy this element belongs to.
	 *
	 * @return the root element, which may be this element itself if it has no parent
	 */
	@Override
	@SuppressWarnings("unchecked")
	default T root()
	{
		return getParent() == null || getParent().getId() == null ? (T) this
				: getParent().root();
	}

	/**
	 * Returns whether this element is the root of its hierarchy.
	 *
	 * @return {@code true} if this element has no parent or its parent has no id
	 */
	@Override
	default boolean isRoot()
	{
		return getParent() == null || getParent().getId() == null;
	}

	/**
	 * Checks if this element is equal to or an ancestor of the specified element.
	 *
	 * @param entity the element to check
	 * @return true if this element equals or is a direct or indirect parent of the specified element
	 * @throws NullPointerException if the specified element is null or has a null id
	 */
	default boolean contains(T entity)
	{
		Objects.requireNonNull(entity);
		Objects.requireNonNull(entity.getId());
		return equals(entity) || isAncestorOf(entity);
	}

	/**
	 * Checks if this element is a descendant of the specified element.
	 *
	 * @param entity the element to check
	 * @return true if this element is a direct or indirect child of the specified element
	 * @throws NullPointerException if the specified element is null or has a null id
	 * @deprecated use {@link #isDescendantOf(Hierarchy)}
	 */
	@Deprecated(since = "21.0.0", forRemoval = true)
	default boolean isChildOf(T entity)
	{
		return isDescendantOf(entity);
	}

	/**
	 * Checks if this element is a descendant of the specified element.
	 *
	 * @param entity the element to check
	 * @return true if this element is a direct or indirect child of the specified element
	 * @throws NullPointerException if the specified element is null or has a null id
	 */
	@Override
	default boolean isDescendantOf(T entity)
	{
		Objects.requireNonNull(entity);
		Objects.requireNonNull(entity.getId());
		return getParent() != null && getParent().getId() != null
				&& (getParent().equals(entity) || getParent().isDescendantOf(entity));
	}

	/**
	 * Checks if this element is equal to or a descendant of the specified element.
	 *
	 * @param entity the element to check
	 * @return true if this element equals or is a direct or indirect child of the specified element
	 * @throws NullPointerException if the specified element is null or has a null id
	 * @deprecated use {@link #isInSubtreeOf(Hierarchy)}
	 */
	@Deprecated(since = "21.0.0", forRemoval = true)
	default boolean isContainedBy(T entity)
	{
		return isInSubtreeOf(entity);
	}

	/**
	 * Checks if this element is equal to or a descendant of the specified element.
	 *
	 * @param entity the element to check
	 * @return true if this element equals or is a direct or indirect child of the specified element
	 * @throws NullPointerException if the specified element is null or has a null id
	 */
	@Override
	default boolean isInSubtreeOf(T entity)
	{
		Objects.requireNonNull(entity);
		Objects.requireNonNull(entity.getId());
		return equals(entity) || isDescendantOf(entity);
	}

	/**
	 * Returns a stream of this element and all its descendants recursively.
	 *
	 * @return a stream containing this element followed by all descendants in depth-first order
	 * @deprecated use {@link #subtree()}
	 */
	@Deprecated(since = "21.0.0", forRemoval = true)
	default Stream<T> stream()
	{
		return subtree();
	}

	/**
	 * Searches for the element with the specified id within this element and its descendants.
	 *
	 * @param id the id to search for
	 * @return an Optional containing the element whose id equals the specified id,
	 * or an empty Optional if not found
	 */
	@SuppressWarnings("unchecked")
	default Optional<T> select(ID id)
	{
		return getId().equals(id) ? Optional.of((T) this)
				: getChildren().stream().map(e -> e.select(id)).flatMap(Optional::stream).findFirst();
	}

	/**
	 * Returns a list of values extracted from this element and all its ancestors recursively.
	 *
	 * @param <E>       the type of the extracted value
	 * @param extractor the function used to extract a value from each element
	 * @return a list of extracted values from this element up to the root
	 * @deprecated use {@link #lineage()}
	 */
	@Deprecated(since = "21.0.0", forRemoval = true)
	default <E> List<E> toParentList(Function<T, E> extractor)
	{
		return lineage().map(extractor).collect(Collectors.toList());
	}

	/**
	 * Returns a list of this element and all its ancestors recursively.
	 *
	 * @return a list containing this element followed by its ancestors up to the root
	 * @deprecated use {@link #lineage()}
	 */
	@Deprecated(since = "21.0.0", forRemoval = true)
	default List<T> toParentList()
	{
		return lineage().collect(Collectors.toList());
	}

	/**
	 * Returns the direct parent and its ancestors up to the root.
	 *
	 * @return parent, grandparent and so on up to the root
	 */
	@Override
	default Stream<T> ancestors()
	{
		return getParent() == null || getParent().getId() == null
				? Stream.empty()
				: Stream.concat(Stream.of(getParent()), getParent().ancestors());
	}

	/**
	 * Returns the path from the root element to this element.
	 *
	 * @return path from the root to this element
	 */
	@Override
	@SuppressWarnings("unchecked")
	default Stream<T> path()
	{
		return getParent() == null || getParent().getId() == null
				? Stream.of((T) this)
				: Stream.concat(getParent().path(), Stream.of((T) this));
	}

	/**
	 * Returns the siblings of this element.
	 *
	 * @return elements sharing the same parent, excluding this element
	 */
	@Override
	default Stream<T> siblings()
	{
		return getParent() == null || getParent().getId() == null
				? Stream.empty()
				: getParent().getChildren().stream().filter(e -> !e.equals(this));
	}

	/**
	 * Returns a stream of this element and all its ancestors recursively.
	 *
	 * @return a stream containing this element followed by its ancestors up to the root
	 * @deprecated use {@link #lineage()}
	 */
	@Deprecated(since = "21.0.0", forRemoval = true)
	default Stream<T> parentStream()
	{
		return lineage();
	}

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

	@Override
	String toString();

	/**
	 * Assembles a flat list of elements into a proper hierarchy and validates the result.
	 * <p>
	 * For each element, the parent stub (which typically contains only the id) is replaced
	 * with the full parent object found in the list, and each element's children list is
	 * populated with its direct children. <strong>This method mutates the elements in the list.</strong>
	 * <p>
	 * Validation is performed before assembly:
	 * <ul>
	 *   <li>Duplicate elements cause a {@link HierarchyException}</li>
	 *   <li>Direct and indirect circular references cause a {@link HierarchyException}</li>
	 *   <li>A parent reference pointing to an element not present in the list causes a {@link HierarchyException}</li>
	 * </ul>
	 *
	 * @param <T>  the concrete hierarchy type
	 * @param list the flat list of elements to assemble; its elements will be mutated
	 * @return a list containing only the root elements of the assembled hierarchy
	 * @throws HierarchyException   if the list contains duplicates, a circular reference, or an unknown parent
	 * @throws NullPointerException if the list is null or contains any element with a null id
	 */
	static <T extends Hierarchy<T>> List<T> setup(List<T> list) throws HierarchyException
	{
		Objects.requireNonNull(list);

		for (int i = 0; i < list.size(); i++)
			for (int j = i + 1; j < list.size(); j++)
				if (list.get(i).equals(list.get(j)))
					throw new HierarchyException("Duplicate entry: " + list.get(i));

		for (T object : list)
		{
			Objects.requireNonNull(object.getId());
			Set<ID> visited = new HashSet<>();
			for (T parent = object.getParent(); parent != null && parent.getId() != null; parent =
					parent.getParent())
			{
				if (!visited.add(parent.getId()))
					throw new HierarchyException(
							String.format("Circular reference detected between %s and %s",
									parent.getId(), object.getId()));

				final T _parent = parent;
				parent = list.stream().filter(e -> e.equals(_parent)).findAny()
						.orElseThrow(() -> new HierarchyException(
								"Unknown parent reference found while assembling hierarchy: "
										+ _parent.getId()));
			}
		}

		list.stream()
				.filter(c -> c.getParent() != null
						&& c.getParent().getId() != null)
				.forEach(c ->
				{
					T parent = list.stream()
							.filter(p -> Objects.equals(c.getParent(), p))
							.findAny()
							.orElseThrow();

					if (c.getParent() != parent)
						c.setParent(parent);
				});

		list.forEach(p ->
		{
			List<T> children = list.stream()
					.filter(c -> Objects.equals(c.getParent(), p))
					.toList();

			if (!Objects.equals(p.getChildren(), children))
				p.setChildren(children);
		});

		return list.stream()
				.filter(e -> e.getParent() == null || e.getParent().getId() == null)
				.collect(Collectors.toList());
	}

}