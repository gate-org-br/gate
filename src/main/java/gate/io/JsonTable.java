package gate.io;

import gate.annotation.SecurityKey;
import gate.converter.Converter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

class JsonTable<T> extends AbstractTable<T>
{

	private JsonTable(Class<T> type, File file, Set<T> values)
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

				byte[] bytes = Converter.toJson(values).getBytes();

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

	static <T> JsonTable<T> create(Class<T> type, File file)
	{
		if (!file.exists() || file.length() == 0)
			return new JsonTable<>(type, file, new HashSet<>());

		try
		{
			byte[] bytes = Files.readAllBytes(file.toPath());
			bytes = Compactor.extract(bytes);

			if (type.isAnnotationPresent(SecurityKey.class))
				bytes = Encryptor.of(ALGORITHM, type.getAnnotation(SecurityKey.class).value()).decrypt(bytes);

			Set<T> values = Converter.fromJson(Set.class, type, new String(bytes));
			return new JsonTable<>(type, file, values != null ? values : new HashSet<>());
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
