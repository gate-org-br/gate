package gate.converter;

import gate.error.ConversionException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
		return new NormalEncoder<>(type);
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
			var json = Converter.toJson(object);
			return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
		}

		@Override
		public T decode(String string) throws ConversionException
		{
			if (string == null)
				return null;
			string = string.trim();
			if (string.isEmpty())
				return null;
			var json = new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8);
			return Converter.fromJson(type, json);
		}
	}
}
