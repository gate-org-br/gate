package gate.util;

import gate.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class JNDIContextMap<T> extends AbstractMap<String, T>
{

	private Context context;

	public JNDIContextMap(Context context)
	{
		Objects.requireNonNull(context);
		this.context = context;
	}

	public JNDIContextMap(String context)
	{
		try
		{
			this.context = new InitialContext();
			try
			{
				this.context = (Context) this.context.lookup(context);
			} catch (NameNotFoundException e)
			{
				this.context = this.context.createSubcontext(context);
			}
		} catch (NamingException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public int size()
	{
		try
		{
			int i = 0;
			NamingEnumeration<NameClassPair> list = context.list("");
			while (list.hasMoreElements())
				i++;
			return i;
		} catch (NamingException e)
		{
			throw new AppError(e);
		}

	}

	@Override
	public boolean isEmpty()
	{
		try
		{
			NamingEnumeration<NameClassPair> list = context.list("");
			return list.hasMoreElements();
		} catch (NamingException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public boolean containsKey(Object key)
	{
		try
		{
			context.lookup((String) key);
			return true;
		} catch (NameNotFoundException e)
		{
			return false;
		} catch (NamingException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public boolean containsValue(Object value)
	{
		try
		{
			NamingEnumeration<NameClassPair> list = context.list("");
			while (list.hasMoreElements())
				if (context.lookup(list.next().getName()).equals(value))
					return true;
			return false;
		} catch (NamingException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public T get(Object key)
	{
		try
		{
			Object value = context.lookup((String) key);
			Converter converter = Converter.getConverter(Object.class);
			String string = converter.toString(Object.class, value);
			value = converter.ofString(Object.class, string);
			return (T) value;
		} catch (NameNotFoundException e)
		{
			return null;
		} catch (NamingException | ConversionException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public T put(String key, T value)
	{
		try
		{
			try
			{
				Object previous = context.lookup((String) key);
				context.bind(key, value);

				Converter converter = Converter.getConverter(Object.class);
				String string = converter.toString(Object.class, previous);
				previous = converter.ofString(Object.class, string);
				return (T) previous;
			} catch (NameNotFoundException e)
			{
				context.bind(key, value);
				return null;
			}
		} catch (NamingException | ConversionException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public T remove(Object key)
	{
		try
		{
			Object value = context.lookup((String) key);
			context.unbind((String) key);

			Converter converter = Converter.getConverter(Object.class);
			String string = converter.toString(Object.class, value);
			value = converter.ofString(Object.class, string);
			return (T) value;
		} catch (NamingException | ConversionException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public void putAll(Map<? extends String, ? extends T> m)
	{
		for (Map.Entry<? extends String, ? extends T> entry : m.entrySet())
			put(entry.getKey(), entry.getValue());

	}

	@Override
	public void clear()
	{
		try
		{
			NamingEnumeration<NameClassPair> list = context.list("");
			while (list.hasMoreElements())
				context.unbind(list.next().getName());
		} catch (NamingException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public Set<String> keySet()
	{
		try
		{
			Set<String> keys = new HashSet<>();
			NamingEnumeration<NameClassPair> list = context.list("");
			while (list.hasMoreElements())
				keys.add(list.next().getName());
			return keys;
		} catch (NamingException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public Collection<T> values()
	{
		try
		{
			Set<T> values = new HashSet<>();
			NamingEnumeration<NameClassPair> list = context.list("");
			while (list.hasMoreElements())
				values.add(get(list.next().getName()));
			return values;
		} catch (NamingException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public Set<Entry<String, T>> entrySet()
	{
		try
		{
			Set<Entry<String, T>> entries = new HashSet<>();
			NamingEnumeration<NameClassPair> list = context.list("");
			while (list.hasMoreElements())
			{
				String name = list.next().getName();
				entries.add(new SimpleEntry<>(name, get(name)));
			}

			return entries;
		} catch (NamingException e)
		{
			throw new AppError(e);
		}
	}
}
