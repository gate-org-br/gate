package gate.io;

import gate.error.ConversionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer
{

	private Serializer()
	{

	}

	public static byte[] serialize(Object object) throws ConversionException
	{
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
		{
			try (ObjectOutputStream objectOutputStream
					= new ObjectOutputStream(byteArrayOutputStream))
			{
				objectOutputStream.writeObject(object);
				objectOutputStream.flush();
			}

			return byteArrayOutputStream.toByteArray();
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		}
	}

	public static Object deserialize(byte[] bytes) throws ConversionException
	{
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes))
		{
			try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream))
			{
				return objectInputStream.readObject();
			} catch (ClassNotFoundException ex)
			{
				throw new IOException(ex.getMessage(), ex);
			}
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		}
	}
}
