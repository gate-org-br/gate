package gate.type.mime;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

public final class MimeMail<T extends Mime> implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final T content;
	private final String subject;

	public MimeMail(String subject, T content)
	{
		Objects.requireNonNull(subject, "Mime mail subject cannot be null");
		Objects.requireNonNull(content, "Mime mail content cannot be null");

		this.subject = subject;
		this.content = content;
	}

	public String getSubject()
	{
		return subject;
	}

	public T getContent()
	{
		return content;
	}

	@Override
	public String toString()
	{
		return subject;
	}

	/**
	 * Creates an empty multi part MimeMail with the specified subject.
	 *
	 * @param subject the subject of the MimeMail to be created
	 *
	 * @return an empty multi part MimeMail with the specified subject
	 */
	public static MimeMail<MimeList> of(String subject)
	{
		return new MimeMail<>(subject, new MimeList());
	}

	/**
	 * Creates a multi part MimeMail with the specified subject and contents.
	 *
	 * @param subject the subject of the MimeMail to be created
	 * @param mime the contents of the MimeMail to be created
	 *
	 * @return a multi part MimeMail with the specified subject and contents
	 */
	public static MimeMail<MimeList> of(String subject, Mime... mime)
	{
		MimeList mimeList = new MimeList();
		Stream.of(mime).forEach(e -> mimeList.add(e));
		return new MimeMail<>(subject, mimeList);
	}

	/**
	 * Creates a MimeMail with the specified subject and text.
	 *
	 * @param subject the subject of the MimeMail to be created
	 * @param text the text of the MimeMail to be created
	 *
	 * @return a MimeMail with the specified subject text
	 */
	public static MimeMail<MimeText> of(String subject, String text)
	{
		return new MimeMail<>(subject, new MimeText(text));
	}

	/**
	 * Creates a MimeMail with the specified subject and attached text file.
	 *
	 * @param subject the subject of the MimeMail to be created
	 * @param text the contents of the text file to be attached
	 * @param name the name of the text file to be attached
	 *
	 * @return a MimeMail with the specified subject and attached file
	 */
	public static MimeMail<MimeText> of(String subject, String text, String name)
	{
		return new MimeMail<>(subject, new MimeTextFile(text, name));
	}

	/**
	 * Creates a MimeMail with the specified subject and attached binary data.
	 *
	 * @param subject the subject of the MimeMail to be created
	 * @param data the binary content of the MimeMail to be created
	 *
	 * @return a MimeMail with the specified subject text
	 */
	public static MimeMail<MimeData> of(String subject, byte[] data)
	{
		return new MimeMail<>(subject, new MimeData(data));
	}

	/**
	 * Creates a MimeMail with the specified subject and attached binary file.
	 *
	 * @param subject the subject of the MimeMail to be created
	 * @param data the contents of the binary file to be attached
	 * @param name the name of the binary file to be attached
	 *
	 * @return a MimeMail with the specified subject and attached file
	 */
	public static MimeMail<MimeData> of(String subject, byte[] data, String name)
	{
		return new MimeMail<>(subject, new MimeDataFile(data, name));
	}
}
