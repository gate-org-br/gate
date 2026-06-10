package gate.type;

import gate.error.HierarchyException;

import java.util.List;
import java.util.Objects;
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
public interface Hierarchy<T extends Hierarchy<T>>
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
	 */
	default boolean isParentOf(T entity)
	{
		Objects.requireNonNull(entity);
		return getChildren().stream().anyMatch(e -> e.equals(entity) || e.isParentOf(entity));
	}

	/**
	 * Returns the root of the hierarchy this element belongs to.
	 *
	 * @return the root element, which may be this element itself if it has no parent
	 */
	@SuppressWarnings("unchecked")
	default T getRoot()
	{
		return getParent() == null || getParent().getId() == null ? (T) this
				: getParent().getRoot();
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
		return equals(entity) || isParentOf(entity);
	}

	/**
	 * Checks if this element is a descendant of the specified element.
	 *
	 * @param entity the element to check
	 * @return true if this element is a direct or indirect child of the specified element
	 * @throws NullPointerException if the specified element is null or has a null id
	 */
	default boolean isChildOf(T entity)
	{
		Objects.requireNonNull(entity);
		Objects.requireNonNull(entity.getId());
		return getParent().getId() != null
				&& (getParent().equals(entity) || getParent().isChildOf(entity));
	}

	/**
	 * Checks if this element is equal to or a descendant of the specified element.
	 *
	 * @param entity the element to check
	 * @return true if this element equals or is a direct or indirect child of the specified element
	 * @throws NullPointerException if the specified element is null or has a null id
	 */
	default boolean isContainedBy(T entity)
	{
		Objects.requireNonNull(entity);
		Objects.requireNonNull(entity.getId());
		return equals(entity) || isChildOf(entity);
	}

	/**
	 * Returns a stream of this element and all its descendants recursively.
	 *
	 * @return a stream containing this element followed by all descendants in depth-first order
	 */
	@SuppressWarnings("unchecked")
	default Stream<T> stream()
	{
		return Stream.concat((Stream<T>) Stream.of(this),
				getChildren().stream().flatMap(Hierarchy::stream));
	}

	/**
	 * Returns a list of this element and all its descendants recursively.
	 *
	 * @return a list containing this element followed by all descendants in depth-first order
	 */
	default List<T> toList()
	{
		return stream().collect(Collectors.toList());
	}

	/**
	 * Returns a list of values extracted from this element and all its descendants recursively.
	 *
	 * @param <E>       the type of the extracted value
	 * @param extractor the function used to extract a value from each element
	 * @return a list of extracted values in depth-first order
	 */
	default <E> List<E> toList(Function<T, E> extractor)
	{
		return stream().map(extractor).collect(Collectors.toList());
	}

	/**
	 * Searches for the element with the specified id within this element and its descendants.
	 *
	 * @param id the id to search for
	 * @return the element whose id equals the specified id, or null if not found
	 */
	@SuppressWarnings("unchecked")
	default T select(ID id)
	{
		return getId().equals(id) ? (T) this
				: getChildren().stream().map(e -> e.select(id)).filter(Objects::nonNull).findFirst()
				  .orElse(null);
	}

	/**
	 * Returns a list of values extracted from this element and all its ancestors recursively.
	 *
	 * @param <E>       the type of the extracted value
	 * @param extractor the function used to extract a value from each element
	 * @return a list of extracted values from this element up to the root
	 */
	default <E> List<E> toParentList(Function<T, E> extractor)
	{
		return parentStream().map(extractor).collect(Collectors.toList());
	}

	/**
	 * Returns a list of this element and all its ancestors recursively.
	 *
	 * @return a list containing this element followed by its ancestors up to the root
	 */
	default List<T> toParentList()
	{
		return parentStream().collect(Collectors.toList());
	}

	/**
	 * Returns a stream of this element and all its ancestors recursively.
	 *
	 * @return a stream containing this element followed by its ancestors up to the root
	 */
	@SuppressWarnings("unchecked")
	default Stream<T> parentStream()
	{
		return getParent().getId() == null ? Stream.of((T) this)
				: Stream.concat(Stream.of((T) this), getParent().parentStream());
	}

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

	@Override
	String toString();

	/**
	 * Assembles a flat list of elements into a proper hierarchy by wiring parent-child
	 * relationships and validates the result for duplicates and circular references.
	 * <p>
	 * Each element's parent reference is resolved against the list, and children are
	 * populated accordingly. Direct and indirect circular references are both detected.
	 *
	 * @param <T>  the concrete hierarchy type
	 * @param list the flat list of elements to assemble
	 * @return a list containing only the root elements of the assembled hierarchy
	 * @throws HierarchyException   if the list contains duplicate elements or a circular reference
	 * @throws NullPointerException if the list is null or contains any element with a null id
	 */
	static <T extends Hierarchy<T>> List<T> setup(List<T> list) throws HierarchyException
	{
		Objects.requireNonNull(list);

		for (int i = 0; i < list.size(); i++)
			for (int j = i + 1; j < list.size(); j++)
				if (list.get(i).equals(list.get(j)))
					throw new HierarchyException("Registro duplicado: " + list.get(i));

		for (T object : list)
		{
			Objects.requireNonNull(object.getId());

			for (T parent = object.getParent(); parent != null && parent.getId() != null; parent =
					parent.getParent())
			{
				if (parent.equals(object))
					throw new HierarchyException(
							String.format("Relação circular encontrada entre %s and %s",
									parent.getId(), object.getId()));

				final T _parent = parent;
				parent = list.stream().filter(e -> e.equals(_parent)).findAny()
						.orElseThrow(() -> new HierarchyException(
								"Registro inexistente encontrado ao montar hierarquia: "
										+ _parent.getId()));
			}
		}

		list.forEach(p -> p.setChildren(list.stream().filter(c -> Objects.equals(c.getParent(), p))
				.peek(c -> c.setParent(p)).collect(Collectors.toList())));

		return list.stream().filter(e -> e.getParent() == null || e.getParent().getId() == null)
				.collect(Collectors.toList());
	}

}