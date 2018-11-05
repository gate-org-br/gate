package gate.tags.formControls;

import gate.converter.Converter;
import gate.error.ConversionException;
import gate.lang.property.Property;
import gate.util.PropertyComparator;
import gate.util.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspException;

abstract class SelectorTag extends PropertyTag
{

	private Object options;
	private String orderedBy;
	private String labeledBy;
	private String groupedBy;
	private String identifiedBy;
	private Map<String, List<Option>> groups;

	public void setOrderedBy(String orderedBy)
	{
		this.orderedBy = orderedBy;
	}

	public void setIdentifiedBy(String identifiedBy)
	{
		this.identifiedBy = identifiedBy;
	}

	public void setLabeledBy(String labeledBy)
	{
		this.labeledBy = labeledBy;
	}

	public void setGroupedBy(String groupedBy)
	{
		this.groupedBy = groupedBy;
	}

	public void setOptions(Object options)
	{
		this.options = options;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		List<Object> options = new ArrayList<>(Toolkit.list(this.options));

		if (options.isEmpty())
		{
			if (Enum.class.isAssignableFrom(getType()))
				options.addAll(Arrays.asList(getType().getEnumConstants()));
			else if (Boolean.class.isAssignableFrom(getType()))
				options.addAll(Arrays.asList(Boolean.FALSE, Boolean.TRUE));
		}

		if (orderedBy != null)
			Collections.sort(options, new PropertyComparator(orderedBy));

		Collection<?> selectedValues = Toolkit.collection(getValue());
		groups = new LinkedHashMap<>();
		for (Object obj : options)
		{
			Option option = new Option();
			Object value = identifiedBy != null
					? Property.getValue(obj, identifiedBy) : obj;
			option.value = Converter.toString(value);
			option.selected = selectedValues.contains(value);
			option.label = Converter.toText(labeledBy != null ? Property.getValue(obj, labeledBy) : obj);
			option.group = groupedBy != null ? Converter.toText(Property.getValue(obj, groupedBy)) : null;
			if (!groups.containsKey(option.group))
				groups.put(option.group, new ArrayList<>());
			groups.get(option.group).add(option);
		}
	}

	public Map<String, List<Option>> getGroups() throws ConversionException
	{
		return groups;
	}

	protected static class Option implements Comparable<Option>
	{

		private String label;
		private String value;
		private String group;
		private boolean selected;

		public String getLabel()
		{
			return label;
		}

		public String getValue()
		{
			return value;
		}

		public String getGroup()
		{
			return group;
		}

		public boolean getSelected()
		{
			return selected;
		}

		@Override
		public int compareTo(Option option)
		{
			return getLabel().compareTo(option.getLabel());
		}
	}
}
