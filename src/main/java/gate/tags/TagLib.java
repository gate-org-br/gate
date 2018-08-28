package gate.tags;

import gate.annotation.Color;
import gate.annotation.Description;
import gate.annotation.Name;
import gate.error.AppError;
import gate.util.Icons;
import java.lang.reflect.AnnotatedElement;

public class TagLib
{

	public static String icon(Object type)
	{
		return Icons.getInstance()
				.get(type, null).toString();
	}

	public static String name(Object obj)
	{
		try
		{
			if (obj == null)
				return "Unnamed";
			AnnotatedElement type = obj instanceof Enum<?> ? obj.getClass().getField(((Enum<?>) obj).name()) : obj
					.getClass();
			return type.isAnnotationPresent(Name.class) ? type.getAnnotation(Name.class).value() : "Unnamed";
		} catch (NoSuchFieldException | SecurityException ex)
		{
			throw new AppError(ex);
		}
	}

	public static String color(Object obj)
	{
		try
		{
			if (obj == null)
				return "#000000";
			AnnotatedElement type = obj instanceof Enum<?> ? obj.getClass().getField(((Enum<?>) obj).name()) : obj
					.getClass();
			return type.isAnnotationPresent(Color.class) ? type.getAnnotation(Color.class).value() : "#000000";
		} catch (NoSuchFieldException | SecurityException ex)
		{
			throw new AppError(ex);
		}
	}

	public static String description(Object obj)
	{
		try
		{
			if (obj == null)
				return "Undescribed";
			AnnotatedElement type = obj instanceof Enum<?> ? obj.getClass().getField(((Enum<?>) obj).name()) : obj
					.getClass();
			return type.isAnnotationPresent(Description.class) ? type.getAnnotation(Description.class).value() : "Undescribed";
		} catch (NoSuchFieldException | SecurityException ex)
		{
			throw new AppError(ex);
		}
	}

}
