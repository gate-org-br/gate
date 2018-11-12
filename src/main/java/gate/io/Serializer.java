package gate.io;

import gate.error.ConversionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;

public class Serializer<T>
{

	private Serializer()
	{
	}

	public static <T> Serializer<T> of(Class<T> type)
	{
		return new Serializer<>();
	}

	public byte[] serialize(T object)
	{
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream))
		{
			objectOutputStream.writeObject(object);
			objectOutputStream.flush();
			return byteArrayOutputStream.toByteArray();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("unchecked")
	public T deserialize(byte[] bytes) throws ConversionException
	{
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
				ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream))
		{
			return (T) objectInputStream.readObject();
		} catch (IOException | ClassNotFoundException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		}
	}
}
