package gate.sql;

import gate.converter.Converter;
import gate.error.ConstraintViolationException;
import gate.error.DatabaseException;
import gate.sql.fetcher.Fetcher;
import gate.sql.mapper.Mapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
			throw new UnsupportedOperationException(e);
		}
	}

	public void setQueryTimeout(int max)
	{
		try
		{
			ps.setQueryTimeout(max);
		} catch (SQLException e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	public int getMaxRows()
	{
		try
		{
			return ps.getMaxRows();
		} catch (SQLException e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	public Cursor getCursor()
	{
		try
		{
			return new Cursor(this, ps.executeQuery());
		} catch (SQLException e)
		{
			throw new UnsupportedOperationException(e);
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

	public int execute() throws ConstraintViolationException
	{
		try
		{
			reset();
			return ps.executeUpdate();
		} catch (SQLException e)
		{
			DatabaseException.handle(link, e);
			throw new UnsupportedOperationException(e);
		}
	}

	public <T> Optional<T> execute(Class<T> type)
		throws ConstraintViolationException
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
			throw new UnsupportedOperationException(e);
		}
	}

	public <T> Optional<T> getGeneratedKey(Class<T> type)
	{
		try ( Cursor cursor = getGeneratedKeys())
		{
			return cursor.next()
				? Optional.of(cursor.getCurrentValue(type))
				: Optional.empty();
		}
	}

	public <T> List<T> getGeneratedKeys(Class<T> type)
	{
		try ( Cursor cursor = getGeneratedKeys())
		{
			List<T> keys = new ArrayList<>();
			while (cursor.next())
				keys.add(cursor.getCurrentValue(type));
			return keys;
		}
	}

	public boolean isClosed()
	{
		try
		{
			return ps.isClosed();
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	@Override
	public void close()
	{
		try
		{
			ps.close();
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	public int setChar(int index, char value)
	{
		try
		{
			ps.setInt(index, value);
			return index++;
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	public int setBoolean(int index, boolean value)
	{
		try
		{
			ps.setBoolean(index, value);
			return index++;
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	public int setByte(int index, byte value)
	{
		try
		{
			ps.setByte(index, value);
			return index++;
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	public int setShort(int index, short value)
	{
		try
		{
			ps.setShort(index, value);
			return index++;
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	public int setInt(int index, int value)
	{
		try
		{
			ps.setInt(index, value);
			return index++;
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	public int setLong(int index, long value)
	{
		try
		{
			ps.setLong(index, value);
			return index++;
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	public int setFloat(int index, float value)
	{
		try
		{
			ps.setFloat(index, value);
			return index++;
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	public int setDouble(int index, double value)
	{
		try
		{
			ps.setDouble(index, value);
			return index++;
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}

	public int setNull(int index)
	{
		try
		{
			ps.setNull(index, Types.VARCHAR);
			return index + 1;
		} catch (SQLException e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	public int setParameter(Class<?> type, int index, Object object)
	{
		try
		{
			return Converter.getConverter(type)
				.writeToPreparedStatement(getPreparedStatement(), index, object);
		} catch (SQLException ex)
		{
			throw new UnsupportedOperationException(ex);
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
			return setNull(index);
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
		objs.forEach(this::setParameter);
		return this;
	}

	@Override
	public <T> T fetch(Fetcher<T> fecher)
	{
		try ( Cursor cursor = getCursor())
		{
			return cursor.fetch(fecher);
		}
	}

	@Override
	public <T> Stream<T> stream(Mapper<T> mapper)
	{
		Cursor cursor = getCursor();
		return cursor.stream(mapper).onClose(() -> cursor.close());
	}

	@Override
	public char fetchChar()
	{
		try ( Cursor cursor = getCursor())
		{
			return cursor.fetchChar();
		}
	}

	@Override
	public boolean fetchBoolean()
	{
		try ( Cursor cursor = getCursor())
		{
			return cursor.fetchBoolean();
		}
	}

	@Override
	public byte fetchByte()
	{
		try ( Cursor cursor = getCursor())
		{
			return cursor.fetchByte();
		}
	}

	@Override
	public short fetchShort()
	{
		try ( Cursor cursor = getCursor())
		{
			return cursor.fetchShort();
		}
	}

	@Override
	public int fetchInt()
	{
		try ( Cursor cursor = getCursor())
		{
			return cursor.fetchInt();
		}
	}

	@Override
	public long fetchLong()
	{
		try ( Cursor cursor = getCursor())
		{
			return cursor.fetchInt();
		}
	}

	@Override
	public float fetchFloat()
	{
		try ( Cursor cursor = getCursor())
		{
			return cursor.fetchFloat();
		}
	}

	@Override
	public double fetchDouble()
	{
		try ( Cursor cursor = getCursor())
		{
			return cursor.fetchDouble();
		}
	}
}
