package gate.adapter.converter;


import gate.error.ConversionException;
import gate.lang.json.JsonElement;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class Encoder<T>
{

	protected final Type type;

	private Encoder(Type type)
	{
		this.type = type;
	}

	public abstract String encode(T object);

	public abstract T decode(String string) throws ConversionException;

	public static <T> Encoder<T> of(Type type)
	{
		return new NormalEncoder<>(type);
	}

	private static class NormalEncoder<T> extends Encoder<T>
	{

		private NormalEncoder(Type type)
		{
			super(type);
		}

		@Override
		public String encode(T object)
		{
			if (object == null)
				return "";
			var jsonElement = JsonElement.encode(object);
			var string = jsonElement.toString().getBytes(StandardCharsets.UTF_8);
			return Base64.getEncoder().encodeToString(string);
		}

		@Override
		public T decode(String string) throws ConversionException
		{
			try
			{
				if (string == null)
					return null;
				string = string.trim();
				if (string.isEmpty())
					return null;
				var json = new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8);

				JsonElement jsonElement = JsonElement.parse(json);
				return jsonElement.decode(type);
			} catch (Exception ex)
			{
				throw new ConversionException("%s is not a valid encoded %s"
						.formatted(string, type.getTypeName()), ex);
			}
		}
	}
}