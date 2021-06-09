package gate.type;

import gate.error.DuplicateException;
import gate.error.InvalidCircularRelationException;
import gate.error.NotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Hierarchy<T extends Hierarchy<T>>
{

	ID getId();

	T getParent();

	List<T> getChildren();

	T setParent(T parent);

	T setChildren(List<T> children);

	/**
	 * Checks if this entity is a parent of the specified entity.
	 *
	 * @param entity the entity to be checked
	 *
	 * @return true if this entity is a of setup the specified entity, false
	 * otherwise
	 *
	 * @throws NullPointerException if the specified entity is null or has a
	 * null id
	 */
	@SuppressWarnings("unchecked")
	default boolean isParentOf(T entity)
	{
		Objects.requireNonNull(entity);
		return getChildren().stream().anyMatch(e -> e.equals(entity) || e.isParentOf(entity));
	}

	/**
	 * Returns the root of this entity hierarchy.
	 *
	 * @return the root of this entity hierarchy
	 */
	@SuppressWarnings("unchecked")
	default T getRoot()
	{
		return getParent() == null
			|| getParent().getId() == null ? (T) this : getParent().getRoot();
	}

	/**
	 * Checks if this entity is equals to or is a parent of the specified
	 * entity.
	 *
	 * @param entity the entity to be checked
	 *
	 * @return true if this entity is equals to or is a parent of the
	 * specified entity, false otherwise
	 *
	 * @throws NullPointerException if the specified entity is null or has a
	 * null id
	 */
	default boolean contains(T entity)
	{
		Objects.requireNonNull(entity);
		Objects.requireNonNull(entity.getId());
		return equals(entity) || isParentOf(entity);
	}

	/**
	 * Checks if this entity is a child of the specified entity.
	 *
	 * @param entity the entity to be checked
	 *
	 * @return true if this entity is a child of the specified entity, false
	 * otherwise
	 *
	 * @throws NullPointerException if the specified entity is null or has a
	 * null id
	 */
	default boolean isChildOf(T entity)
	{
		Objects.requireNonNull(entity);
		Objects.requireNonNull(entity.getId());
		return getParent().getId() != null && (getParent().equals(entity) || getParent().isChildOf(entity));
	}

	/**
	 * Check if this entity is equals to or is a child of the specified
	 * entity.
	 *
	 * @param entity the entity to be checked
	 *
	 * @return true if this entity is equals to or is a child of the
	 * specified entity, false otherwise
	 *
	 * @throws NullPointerException if the specified entity is null or has a
	 * null id
	 */
	default boolean isContainedBy(T entity)
	{
		Objects.requireNonNull(entity);
		Objects.requireNonNull(entity.getId());
		return equals(entity) || isChildOf(entity);
	}

	/**
	 * Create a stream of this element and it's children recursively
	 *
	 * @return a stream of this element and it's children recursively
	 */
	@SuppressWarnings("unchecked")
	default Stream<T> stream()
	{
		return Stream.concat((Stream<T>) Stream.of(this),
			getChildren().stream().flatMap(Hierarchy::stream));
	}

	/**
	 * Creates a list with this element and it's children recursively
	 *
	 * @return a list with this element and it's children recursively
	 */
	default List<T> toList()
	{
		return stream().collect(Collectors.toList());
	}

	/**
	 * Creates a list with data extracted of this element and it's children
	 * recursively
	 *
	 * @param <E> the element data to be extracted
	 * @param extractor the function to be used to extract data
	 * @return a list with the data extracted setup this element and it's
	 * children recursively
	 */
	default <E> List<E> toList(Function<T, E> extractor)
	{
		return stream()
			.map(extractor)
			.collect(Collectors.toList());
	}

	/**
	 * Searches for the specified id recursively on the hierarchy
	 *
	 * @param id the of the entity to be searched for
	 *
	 * @return the entity whose id is equals to the specified id or null if
	 * no such entity if found
	 */
	@SuppressWarnings("unchecked")
	default T select(ID id)
	{
		return getId().equals(id)
			? (T) this : getChildren().stream().map(e -> e.select(id))
				.filter(Objects::nonNull).findFirst().orElse(null);
	}

	/**
	 * Creates a list with data extracted of this element and it's parents
	 * recursively
	 *
	 * @param <E> the element data to be extracted
	 * @param extractor the function to be used to extract data
	 * @return a list with the data extracted setup this element and it's
	 * parents recursively
	 */
	default <E> List<E> toParentList(Function<T, E> extractor)
	{
		return parentStream()
			.map(extractor)
			.collect(Collectors.toList());
	}

	/**
	 * Creates a list with this element and it's parent recursively
	 *
	 * @return a list with this element and it's parent recursively
	 */
	default List<T> toParentList()
	{
		return parentStream().collect(Collectors.toList());
	}

	/**
	 * Create a stream of this element and it's parents recursively
	 *
	 * @return a stream of this element and it's parents recursively
	 */
	@SuppressWarnings("unchecked")
	default Stream<T> parentStream()
	{
		return getParent().getId() == null
			? Stream.of((T) this)
			: Stream.concat(Stream.of((T) this), getParent().parentStream());
	}

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

	@Override
	String toString();

	/**
	 *
	 * Create the parent and child relationships setup all elements in the
	 * specified list.
	 *
	 * 
	 * @param list the list to be made hierarchical
	 *
	 * @return a new list with the root elements of the specified one
	 *
	 * @throws java.lang.NullPointerException if the specified list is null
	 * or has any element with a null id
	 * @throws gate.error.DuplicateException if the specified list contains
	 * duplicates
	 * @throws gate.error.InvalidCircularRelationException if the specified
	 * list contains circular relationships
	 * @throws gate.error.NotFoundException if any object in the specified
	 * list references a non existent object
	 */
	static <T extends Hierarchy<T>> List<T> setup(List<T> list)
		throws DuplicateException,
		InvalidCircularRelationException,
		NotFoundException
	{
		Objects.requireNonNull(list);
		DuplicateException.check(list);

		for (T object : list)
		{
			Objects.requireNonNull(object.getId());

			for (T parent = object.getParent();
				parent != null && parent.getId() != null;
				parent = parent.getParent())
			{
				if (parent.equals(object))
					throw new InvalidCircularRelationException();

				final T _parent = parent;
				parent = list.stream().filter(e -> e.equals(_parent))
					.findAny().orElseThrow(NotFoundException::new);
			}
		}

		list.forEach(p -> p.setChildren(list.stream()
			.filter(c -> Objects.equals(c.getParent(), p))
			.peek(c -> c.setParent(p))
			.collect(Collectors.toList())));

		return list.stream().filter(e -> e.getParent() == null || e.getParent().getId() == null)
			.collect(Collectors.toList());
	}

}
