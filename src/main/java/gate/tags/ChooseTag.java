package gate.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ChooseTag
		extends SimpleTagSupport
{

	private OtherwiseTag otherwiseTag;
	private final List<WhenTag> whenTags = new ArrayList<>();

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getJspBody().invoke(null);
		WhenTag whenTag = whenTags.stream()
				.filter(WhenTag::isCondition)
				.findAny().orElse(null);
		if (whenTag != null)
			whenTag.invoke();
		else if (otherwiseTag != null)
			otherwiseTag.invoke();
	}

	public void setOtherwiseTag(OtherwiseTag otherwiseTag)
	{
		this.otherwiseTag = otherwiseTag;
	}

	public OtherwiseTag getOtherwiseTag()
	{
		return otherwiseTag;
	}

	public List<WhenTag> getWhenTags()
	{
		return whenTags;
	}
}
