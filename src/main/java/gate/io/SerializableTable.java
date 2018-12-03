package gate.io;

import gate.annotation.SecurityKey;
import static gate.io.AbstractTable.ALGORITHM;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

class SerializableTable<T extends Serializable> extends AbstractTable<T>
{

	public SerializableTable(Class<T> type, File file, Set<T> values)
	{
		super(type, file, values);
	}

	@Override
	protected void persist()
	{
		try
		{
			if (!values.isEmpty())
			{
				if (file.getParentFile() != null)
					file.getParentFile().mkdirs();

				byte[] bytes = Serializer.of(Object.class).serialize(values);

				if (type.isAnnotationPresent(SecurityKey.class))
					bytes = Encryptor.of(ALGORITHM, type.getAnnotation(SecurityKey.class).value()).encrypt(bytes);

				bytes = Compactor.compact(bytes);

				Files.write(file.toPath(), bytes, StandardOpenOption.CREATE);
			} else
				file.delete();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	static <T extends Serializable> Table<T> create(Class<T> type, File file)
	{
		if (!file.exists() || file.length() == 0)
			return new SerializableTable<>(type, file, new HashSet<>());

		try
		{
			byte[] bytes = Files.readAllBytes(file.toPath());

			bytes = Compactor.extract(bytes);

			if (type.isAnnotationPresent(SecurityKey.class))
				bytes = Encryptor.of(ALGORITHM, type.getAnnotation(SecurityKey.class).value()).decrypt(bytes);

			@SuppressWarnings("unchecked")
			Set<T> values = Serializer.of(Set.class).deserialize(bytes);

			return new SerializableTable<>(type, file, values != null ? values : new HashSet<>());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public Table<T> concurrent()
	{
		return new ConcurrentTable<>(this);
	}

}
