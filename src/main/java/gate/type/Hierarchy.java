package gate.type;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Hierarchy<T extends Hierarchy<T>>
{

	public ID getId();

	public Hierarchy<T> getParent();

	public List<Hierarchy<T>> getChildren();

	public Hierarchy<T> setParent(Hierarchy<T> parent);

	public Hierarchy<T> setChildren(List<Hierarchy<T>> children);

	/**
	 * Checks if this entity is a parent of the specified entity.
	 *
	 * @param entity the entity to be checked
	 *
	 * @return true if this entity is a parent of the specified entity, false otherwise
	 *
	 * @throws NullPointerException if the specified entity is null or has a null id
	 */
	public default boolean isParentOf(Hierarchy<T> entity)
	{
		Objects.requireNonNull(entity);
		return getChildren().stream().anyMatch(e -> e.equals(entity) || e.isParentOf(entity));
	}

	/**
	 * Returns the root of this entity hierarchy.
	 *
	 * @return the root of this entity hierarchy
	 */
	public default Hierarchy<T> getRoot()
	{
		return getParent() == null || getParent().getId() == null ? this : getParent().getRoot();
	}

	/**
	 * Checks if this entity is equals to or is a parent of the specified entity.
	 *
	 * @param entity the entity to be checked
	 *
	 * @return true if this entity is equals to or is a parent of the specified entity, false otherwise
	 *
	 * @throws NullPointerException if the specified entity is null or has a null id
	 */
	public default boolean contains(Hierarchy<T> entity)
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
	 * @return true if this entity is a child of the specified entity, false otherwise
	 *
	 * @throws NullPointerException if the specified entity is null or has a null id
	 */
	public default boolean isChildOf(Hierarchy<T> entity)
	{
		Objects.requireNonNull(entity);
		Objects.requireNonNull(entity.getId());
		return getParent().getId() != null && (getParent().equals(entity) || getParent().isChildOf(entity));
	}

	public default Hierarchy<T> select(ID id)
	{
		return getId().equals(id)
				? this : getChildren().stream().map(e -> e.select(id))
						.filter(e -> e != null).findFirst().orElse(null);
	}

	/**
	 * Checks if this entity is equals to or is a child of the specified entity.
	 *
	 * @param entity the entity to be checked
	 *
	 * @return true if this entity is equals to or is a child of the specified entity, false otherwise
	 *
	 * @throws NullPointerException if the specified entity is null or has a null id
	 */
	public default boolean isContainedBy(Hierarchy<T> entity)
	{
		Objects.requireNonNull(entity);
		Objects.requireNonNull(entity.getId());
		return equals(entity) || isChildOf(entity);
	}

	public default Stream<Hierarchy<T>> stream()
	{
		return Stream.concat(Stream.of(this),
				getChildren().stream().flatMap(Hierarchy<T>::stream));
	}

	public default List<Hierarchy<T>> toList()
	{
		return stream().collect(Collectors.toList());
	}

	public default <E> List<E> toList(Function<Hierarchy<T>, E> extractor)
	{
		return stream()
				.map(extractor)
				.collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object obj);

	@Override
	public int hashCode();

	@Override
	public String toString();
}
