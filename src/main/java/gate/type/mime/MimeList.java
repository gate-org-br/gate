package gate.type.mime;

import gate.lang.contentType.ContentType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public class MimeList implements Mime, Iterable<Mime>
{

	private static final long serialVersionUID = 1L;
	private final ContentType contentType;
	private final List<Mime> list
			= new ArrayList<>();

	private MimeList(ContentType contentType)
	{
		Objects.requireNonNull(contentType, "Mime type cannot be null.");
		this.contentType = contentType;
	}

	public static MimeList of(ContentType contentType)
	{
		return new MimeList(contentType);
	}

	public static MimeList of()
	{
		return new MimeList(ContentType.of("multipart", "mixed"));
	}

	@Override
	public ContentType getContentType()
	{
		return contentType;
	}

	@Override
	public void forEach(
			Consumer<? super Mime> action)
	{
		list.forEach(action);
	}

	@Override
	public Iterator<Mime> iterator()
	{
		return list.iterator();
	}

	@Override
	public Spliterator<Mime> spliterator()
	{
		return list.spliterator();
	}

	@Override
	public String toString()
	{
		return contentType.toString();
	}

	public MimeList add(Mime mime)
	{
		list.add(mime);
		return this;
	}

	public MimeList add(String text)
	{
		return add(MimeText.of(text));
	}

	public MimeList add(String text, String name)
	{
		return add(MimeTextFile.of(text, name));
	}

	public MimeList add(byte[] data)
	{
		return add(MimeData.of(data));
	}

	public MimeList add(byte[] data, String name)
	{
		return add(MimeDataFile.of(data, name));
	}
}
