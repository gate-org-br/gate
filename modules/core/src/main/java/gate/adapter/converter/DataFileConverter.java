package gate.adapter.converter;

import gate.constraint.Constraint;

import gate.error.ConversionException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class DataFileConverter implements Converter
{

	private static final List<String> SUFIXES
			= Arrays.asList("name", "size", "data");

	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string)
			throws ConversionException
	{
		try
		{
			if (string != null && string.trim().length() > 0)
			{
				byte[] bytes = Base64.getDecoder().decode(string);
				try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes)))
				{
					return ois.readObject();
				}
			}

			return null;
		} catch (IOException | ClassNotFoundException e)
		{
			throw new ConversionException(String.format(
					"%s não é um objeto válido.", string));
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		try
		{
			if (object == null)
				return "";
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				try (ObjectOutputStream ous = new ObjectOutputStream(baos))
				{
					ous.writeObject(object);
				}
				return Base64.getEncoder().encodeToString(baos.toByteArray());
			}

		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}