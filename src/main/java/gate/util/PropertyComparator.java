package gate.util;

import java.util.Comparator;
import gate.lang.property.Property;

public class PropertyComparator implements Comparator<Object>
{

	private final String name;
	private final boolean desc;

	public PropertyComparator(String name)
	{
		desc = name.charAt(0) == '-';
		this.name = name.charAt(0) == '-'
				|| name.charAt(0) == '+'
				? name.substring(1) : name;
	}

	@Override
	public int compare(Object obj1, Object obj2)
	{

		Property property = Property.getProperty(obj1.getClass(), name);
		Comparable comparable1 = (Comparable) (desc ? property.getValue(obj2) : property.getValue(obj1));
		Comparable comparable2 = (Comparable) (desc ? property.getValue(obj1) : property.getValue(obj2));

		if (comparable1 == null && comparable2 == null)
			return 0;
		else if (comparable1 != null && comparable2 == null)
			return 1;
		else if (comparable1 == null && comparable2 != null)
			return -1;
		else if (comparable1 != null && comparable2 != null)
			return comparable1.compareTo(comparable2);

		return 0;
	}
}
