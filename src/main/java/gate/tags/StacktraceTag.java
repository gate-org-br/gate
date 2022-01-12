package gate.tags;

import java.io.IOException;

public class StacktraceTag extends AttributeTag
{

	private Throwable exception;

	public void setException(Throwable exception)
	{
		this.exception = exception;
	}

	@Override
	public void doTag() throws IOException
	{
		getJspContext().getOut().println(TagLib.format(exception));
	}
}
