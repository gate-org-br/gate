package gate.sql;

import gate.error.FKViolationException;
import gate.error.UKViolationException;
import gate.error.DatabaseException;
import gate.error.ConstraintViolationException;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.converter.Converter;
import gate.sql.fetcher.Fetcher;
import java.sql.PreparedStatement;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class Command implements AutoCloseable, Fetchable
{

	private int index = 1;
	private final Link link;
	private final PreparedStatement ps;

	Command(Link link, java.sql.PreparedStatement ps)
	{
		this.link = link;
		this.ps = ps;
	}

	public PreparedStatement getPreparedStatement()
	{
		return ps;
	}

	public Link getConnection()
	{
		return link;
	}

	public Command setMaxRows(int max)
	{
		try
		{
			ps.setMaxRows(max);
			return this;
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	public void setQueryTimeout(int max)
	{
		try
		{
			ps.setQueryTimeout(max);
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	public int getMaxRows()
	{
		try
		{
			return ps.getMaxRows();
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	public Cursor getCursor()
	{
		try
		{
			return new Cursor(this, ps.executeQuery());
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	public Command reset()
	{
		setIndex(1);
		return this;
	}

	public Command setIndex(int index)
	{
		this.index = index;
		return this;
	}

	public int getIndex()
	{
		return index;
	}

	public int execute() throws ConstraintViolationException, FKViolationException, UKViolationException
	{
		try
		{
			reset();
			return ps.executeUpdate();
		} catch (SQLException e)
		{
			DatabaseException.handle(link, e);
			throw new AppError(e);
		}
	}

	public <T> Optional<T> execute(Class<T> type)
			throws ConstraintViolationException, FKViolationException, UKViolationException
	{
		execute();
		return getGeneratedKey(type);
	}

	public Cursor getGeneratedKeys()
	{
		try
		{
			return new Cursor(this, ps.getGeneratedKeys());
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	public <T> Optional<T> getGeneratedKey(Class<T> type)
	{
		try (Cursor cursor = getGeneratedKeys())
		{
			return cursor.next()
					? Optional.of(cursor.getCurrentValue(type))
					: Optional.empty();
		}
	}

	public boolean isClosed()
	{
		try
		{
			return ps.isClosed();
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public void close()
	{
		try
		{
			ps.close();
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	public int setParameter(Class<?> type, int index, Object object)
	{
		try
		{
			return Converter.getConverter(type)
					.writeToPreparedStatement(getPreparedStatement(), index, object);
		} catch (SQLException | ConversionException e)
		{
			throw new AppError(e);
		}
	}

	public int setNullParameter(int index)
	{
		try
		{
			ps.setNull(index, Types.VARCHAR);
			return index + 1;
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	public Command setParameter(Class<?> type, Object object)
	{
		setIndex(setParameter(type, getIndex(), object));
		return this;
	}

	public int setParameter(int index, Object object)
	{
		if (object == null)
			return setNullParameter(index);
		return setParameter(object.getClass(), index, object);
	}

	public Command setParameter(Object object)
	{
		setIndex(setParameter(getIndex(), object));
		return this;
	}

	public Command setParameters(Object... objs)
	{
		return setParameters(Arrays.asList(objs));
	}

	public Command setParameters(Collection<?> objs)
	{
		objs.forEach(e -> setParameter(e));
		return this;
	}

	@Override
	public <T> T fetch(Fetcher<T> fecher)
	{
		try (Cursor rs = getCursor())
		{
			return rs.fetch(fecher);
		}
	}
}
