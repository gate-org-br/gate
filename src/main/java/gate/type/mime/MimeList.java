package gate.type.mime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public class MimeList implements Mime, Iterable<Mime>
{

	private static final long serialVersionUID = 1L;
	private final String type;
	private final String subtype;
	private final List<Mime> list
			= new ArrayList<>();

	public MimeList()
	{
		this("multipart", "mixed");
	}

	public MimeList(String type, String subtype)
	{
		Objects.requireNonNull(type, "Mime type cannot be null.");
		Objects.requireNonNull(type, "Mime subtype cannot be null.");
		this.type = type;
		this.subtype = subtype;
	}

	@Override
	public void forEach(
			Consumer<? super Mime> action)
	{
		list.forEach(action);
	}

	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public String getSubType()
	{
		return subtype;
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
		return type;
	}

	public MimeList add(Mime mime)
	{
		list.add(mime);
		return this;
	}

	public MimeList add(String text)
	{
		return add(new MimeText(text));
	}

	public MimeList add(String text, String name)
	{
		return add(new MimeTextFile(text, name));
	}

	public MimeList add(byte[] data)
	{
		return add(new MimeData(data));
	}

	public MimeList add(byte[] data, String name)
	{
		return add(new MimeDataFile(data, name));
	}
}
