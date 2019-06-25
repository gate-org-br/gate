package gate.base;

import gate.constraint.Constraints;
import gate.constraint.Required;
import gate.entity.User;
import gate.error.AppException;
import gate.error.NotFoundException;
import gate.lang.property.Entity;
import gate.type.ID;
import java.util.List;

public class CrudControl<T> extends Control implements Crud<T>
{

	private final Class<T> type;

	public CrudControl(Class<T> type)
	{
		this.type = type;
	}

	@Override
	public List<T> search(T filter)
	{
		try (CrudDao<T> dao = new CrudDao<>(type))
		{
			return dao.search(filter);
		}
	}

	@Override
	public T select(ID id) throws NotFoundException
	{
		try (CrudDao<T> dao = new CrudDao<>(type))
		{
			return dao.select(id);
		}
	}

	@Override
	public void insert(T value) throws AppException
	{
		Constraints.validate(value, Entity.getProperties(User.class, e -> !e.isEntityId()
			&& (!e.getAttributes().get(1).isEntity() || e.getAttributes().get(1).getConstraints().stream().anyMatch(c -> c.getClass() == Required.Implementation.class))));
		try (CrudDao<T> dao = new CrudDao<>(type))
		{
			dao.insert(value);
		}
	}

	@Override
	public void update(T value) throws AppException
	{
		Constraints.validate(value, Entity.getProperties(User.class, e -> !e.getAttributes().get(1).isEntity() || e.getAttributes().get(1).getConstraints()
			.stream().anyMatch(c -> c.getClass() == Required.Implementation.class)));
		try (CrudDao<T> dao = new CrudDao<>(type))
		{
			dao.update(value);
		}
	}

	@Override
	public void delete(T value) throws AppException
	{
		try (CrudDao<T> dao = new CrudDao<>(type))
		{
			dao.delete(value);
		}
	}
}
