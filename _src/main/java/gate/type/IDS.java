package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.IDSConverter;
import gate.annotation.ElementType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ElementType(ID.class)
@SuppressWarnings("serial")
@Converter(IDSConverter.class)
public class IDS extends ArrayList<ID>
{

	public IDS()
	{
	}

	public IDS(String values)
	{
		for (String id : values.split(","))
			if (!id.trim().isEmpty())
				add(new ID(id.trim()));
	}

	public IDS(ID... values)
	{
		addAll(Arrays.asList(values));
	}

	public IDS(List<ID> values)
	{
		addAll(values);
	}

	@Override
	public String toString()
	{
		return stream().filter(Objects::nonNull).map(ID::toString).collect(Collectors.joining(", "));
	}

	@Override
	public ID[] toArray()
	{
		return toArray(new ID[size()]);
	}
}
