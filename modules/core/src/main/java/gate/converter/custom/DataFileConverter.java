package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class DataFileConverter implements Converter
{

	private static final List<String> SUFIXES
		= Arrays.asList("name", "size", "data");

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return null;
	}

	@Override
	public Object ofString(Class<?> type, String string)
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
			throw new AppError(e);
		}
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}


}
