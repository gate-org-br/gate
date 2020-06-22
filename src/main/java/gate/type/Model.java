package gate.type;

import gate.error.AppException;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class Model implements Serializable
{

	private final DataFile attachment;

	public Model(DataFile attachment)
	{
		this.attachment = attachment;
	}

	public DataFile getAttachment()
	{
		return attachment;
	}

	public abstract DataFile newDocument(Object entity) throws AppException;
}
