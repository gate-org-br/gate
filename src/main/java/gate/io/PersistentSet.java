package gate.io;

import gate.annotation.SecurityKey;
import gate.converter.Converter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class PersistentSet<T> extends AbstractSet<T> implements Observable<T>
{

	private final File file;
	private final Class<T> type;
	private final Set<T> values;
	private static final String ALGORITHM = "AES";
	private final List<Observer<T>> observers = new CopyOnWriteArrayList<>();

	private PersistentSet(Class<T> type, File file, Set<T> values)
	{
		this.file = file;
		this.type = type;
		this.values = values;
	}

	public File getFile()
	{
		return file;
	}

	@Override
	public boolean add(T e)
	{
		if (values.add(e))
		{
			observers.forEach(Observer::onUpdate);
			return true;
		}
		return false;
	}

	@Override
	public void addObserver(Observer<T> observer)
	{
		observers.add(observer);
	}

	@Override
	public void remObserver(Observer<T> observer)
	{
		observers.remove(observer);
	}

	@Override
	public java.util.Iterator<T> iterator()
	{
		return new Iterator();
	}

	@Override
	public int size()
	{
		return values.size();
	}

	public class Iterator implements java.util.Iterator<T>
	{

		private final java.util.Iterator<T> iterator;

		public Iterator()
		{
			this.iterator = values.iterator();
		}

		@Override
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		@Override
		public T next()
		{
			return iterator.next();
		}

		@Override
		public void remove()
		{
			iterator.remove();
			observers.forEach(Observer::onUpdate);
		}

		@Override
		public void forEachRemaining(Consumer<? super T> action)
		{
			iterator.forEachRemaining(action);
		}
	}

	public static <T> PersistentSet<T> of(Class<T> type, File file)
	{
		if (!file.exists() || file.length() == 0)
			return new PersistentSet<>(type, file, new HashSet<>());

		try
		{
			byte[] bytes = Files.readAllBytes(file.toPath());

			if (type.isAnnotationPresent(SecurityKey.class))
				bytes = Encryptor.of(ALGORITHM, type.getAnnotation(SecurityKey.class).value()).decrypt(bytes);

			Set<T> values = Converter.fromJson(Set.class, type, new String(bytes));
			return new PersistentSet<>(type, file, values != null ? values : new HashSet<>());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public void commit()
	{
		try
		{
			if (!values.isEmpty())
			{
				if (file.getParentFile() != null)
					file.getParentFile().mkdirs();

				byte[] bytes = Converter.toJson(values).getBytes();

				if (type.isAnnotationPresent(SecurityKey.class))
					bytes = Encryptor.of(ALGORITHM, type.getAnnotation(SecurityKey.class).value()).encrypt(bytes);

				Files.write(file.toPath(), bytes, StandardOpenOption.CREATE);
			} else
				file.delete();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public void rollback()
	{
		this.values.clear();
		if (file.exists() && file.length() > 0)
		{
			try
			{
				byte[] bytes = Files.readAllBytes(file.toPath());
				if (type.isAnnotationPresent(SecurityKey.class))
					bytes = Encryptor.of(ALGORITHM, type.getAnnotation(SecurityKey.class).value()).decrypt(bytes);
				this.values.addAll(Converter.fromJson(Set.class, type, new String(bytes)));
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}
	}
}
