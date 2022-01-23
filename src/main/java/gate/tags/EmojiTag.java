package gate.tags;

import gate.annotation.Color;
import gate.annotation.Emoji;
import gate.error.AppError;
import gate.util.Emojis;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class EmojiTag extends AttributeTag
{

	private Object type;
	private Object empty;

	public void setType(Object type)
	{
		this.type = type;
	}

	public void setEmpty(Object empty)
	{
		this.empty = empty;
	}

	@Override
	public void doTag()
	{
		try
		{
			super.doTag();

			if (type == null)
				type = empty;

			if (!getAttributes().containsKey("style"))
				Color.Extractor.extract(type)
					.ifPresent(e -> getAttributes().put("style", "color: " + e));

			getJspContext().getOut().print("<e>" + Emoji.Extractor.extract(type).orElse(Emojis.UNKNOWN) + "</e>");
		} catch (JspException | IOException e)
		{
			throw new AppError(e);
		}
	}
}
