package gate.io;

import gate.annotation.SecurityKey;
import gate.error.ConversionException;
import javax.xml.bind.DatatypeConverter;

public abstract class Encoder
{

	public abstract <T> String encode(Class<T> type, T object) throws ConversionException;

	public abstract <T> T decode(Class<T> type, String string) throws ConversionException;

	public static Encoder getInstance()
	{
		return new NormalEncoder();
	}

	public static Encoder getInstance(Class<?> type)
	{
		return type.isAnnotationPresent(SecurityKey.class)
				? new CipherEncoder(new Encryptor("AES", type.getAnnotation(SecurityKey.class).value()))
				: new NormalEncoder();
	}

	public static Encoder getInstance(String key)
	{
		return new CipherEncoder(new Encryptor("AES", key));
	}

	public static Encoder getInstance(byte[] key)
	{
		return new CipherEncoder(new Encryptor("AES", key));
	}

	private static class NormalEncoder extends Encoder
	{

		@Override
		public <T> String encode(Class<T> type, T object) throws ConversionException
		{
			return DatatypeConverter.printBase64Binary(Compressor.compress(Serializer.serialize(object)));
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T decode(Class<T> type, String string) throws ConversionException
		{
			return (T) Serializer.deserialize(Compressor.decompress(DatatypeConverter.parseBase64Binary(string)));
		}
	}

	private static class CipherEncoder extends Encoder
	{

		private final Encryptor encryptor;

		public CipherEncoder(Encryptor encryptor)
		{
			this.encryptor = encryptor;
		}

		@Override
		public <T> String encode(Class<T> type, T object) throws ConversionException
		{
			return DatatypeConverter.printBase64Binary(Compressor.compress(encryptor.encrypt(Serializer.serialize(object))));
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T decode(Class<T> type, String string) throws ConversionException
		{
			return (T) Serializer.deserialize(encryptor.decrypt(Compressor.decompress(DatatypeConverter.parseBase64Binary(string))));
		}
	}

}
