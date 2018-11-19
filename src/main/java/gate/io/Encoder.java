package gate.io;

import gate.annotation.SecurityKey;
import gate.error.ConversionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import javax.xml.bind.DatatypeConverter;

public abstract class Encoder<T>
{

	protected final Class<T> type;

	private Encoder(Class<T> type)
	{
		this.type = type;
	}

	public abstract String encode(T object);

	public abstract T decode(String string) throws ConversionException;

	public static <T> Encoder<T> of(Class<T> type)
	{
		return type.isAnnotationPresent(SecurityKey.class)
				? Encoder.of(type, type.getAnnotation(SecurityKey.class).value())
				: new NormalEncoder<>(type);
	}

	public static <T> Encoder<T> of(Class<T> type, String key)
	{
		return new CipherEncoder<>(type, Encryptor.of("AES", key));
	}

	private static class NormalEncoder<T> extends Encoder<T>
	{

		private NormalEncoder(Class<T> type)
		{
			super(type);
		}

		@Override
		public String encode(T object)
		{
			if (object == null)
				return "";

			try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(deflaterOutputStream))

			{
				objectOutputStream.writeObject(object);
				objectOutputStream.flush();
				deflaterOutputStream.finish();
				return DatatypeConverter.printBase64Binary(byteArrayOutputStream.toByteArray());
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex.getMessage(), ex);
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public T decode(String string) throws ConversionException
		{
			if (string == null)
				return null;
			string = string.trim();
			if (string.isEmpty())
				return null;

			try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(string));
					InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
					ObjectInputStream objectInputStream = new ObjectInputStream(inflaterInputStream))

			{
				return (T) objectInputStream.readObject();
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex.getMessage(), ex);
			} catch (ClassNotFoundException ex)
			{
				throw new UncheckedIOException(ex.getMessage(), new IOException(ex));
			}
		}
	}

	private static class CipherEncoder<T> extends Encoder<T>
	{

		private final Encryptor encryptor;

		public CipherEncoder(Class<T> type, Encryptor encryptor)
		{
			super(type);
			this.encryptor = encryptor;
		}

		@Override
		public String encode(T object)
		{
			if (object == null)
				return "";

			return DatatypeConverter.printBase64Binary(Compactor.compact(encryptor.encrypt(Serializer.of(type).serialize(object))));
		}

		@Override
		@SuppressWarnings("unchecked")
		public T decode(String string) throws ConversionException
		{
			if (string == null)
				return null;
			string = string.trim();
			if (string.isEmpty())
				return null;

			return Serializer.of(type).deserialize(encryptor.decrypt(Compactor.extract(DatatypeConverter.parseBase64Binary(string))));
		}
	}

}
