package gate.tags.switchCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class SwitchTag extends SimpleTagSupport
{

	private Object value;
	private DefaultTag defaultTag;
	private final List<CaseTag> caseTags = new ArrayList<>();

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getJspBody().invoke(null);
		CaseTag caseTag = caseTags.stream()
				.filter(e -> Objects.equals(e.getValue(), value)).findAny().orElse(null);
		if (caseTag != null)
			caseTag.invoke();
		else if (defaultTag != null)
			defaultTag.invoke();
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public List<CaseTag> getCaseTags()
	{
		return caseTags;
	}

	public void setDefaultTag(DefaultTag defaultTag)
	{
		this.defaultTag = defaultTag;
	}

	public DefaultTag getDefaultTag()
	{
		return defaultTag;
	}

}
