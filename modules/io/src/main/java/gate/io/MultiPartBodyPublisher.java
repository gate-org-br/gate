package gate.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Supplier;

public class MultiPartBodyPublisher
{

	private final List<Part> parts = new ArrayList<>();
	private final String boundary = UUID.randomUUID().toString();

	public HttpRequest.BodyPublisher build()
	{
		if (parts.isEmpty())
			throw new IllegalStateException("Must have at least one part to build multipart message.");
		addFinalBoundaryPart();
		return HttpRequest.BodyPublishers.ofByteArrays(PartsIterator::new);
	}

	public String getBoundary()
	{
		return boundary;
	}

	public MultiPartBodyPublisher addPart(String name, String value)
	{
		Part part = new Part();
		part.type = Part.TYPE.STRING;
		part.name = name;
		part.value = value;
		parts.add(part);
		return this;
	}

	public MultiPartBodyPublisher addPart(String name, Path value)
	{
		Part part = new Part();
		part.type = Part.TYPE.FILE;
		part.name = name;
		part.path = value;
		parts.add(part);
		return this;
	}

	public MultiPartBodyPublisher addPart(String name, Supplier<InputStream> value, String contentType, String filename)
	{
		Part part = new Part();
		part.type = Part.TYPE.STREAM;
		part.name = name;
		part.stream = value;
		part.filename = filename;
		part.contentType = contentType;
		parts.add(part);
		return this;
	}

	public MultiPartBodyPublisher addPart(String name, byte[] bytes, String contentType, String filename)
	{
		Part part = new Part();
		part.type = Part.TYPE.STREAM;
		part.name = name;
		part.stream = () -> new ByteArrayInputStream(bytes);
		part.filename = filename;
		part.contentType = contentType;
		parts.add(part);
		return this;
	}

	private void addFinalBoundaryPart()
	{
		Part newPart = new Part();
		newPart.type = Part.TYPE.FINAL_BOUNDARY;
		newPart.value = "--" + boundary + "--";
		parts.add(newPart);
	}

	static class Part
	{

		public enum TYPE
		{
			STRING, FILE, STREAM, FINAL_BOUNDARY
		}

		Part.TYPE type;
		String name;
		String value;
		Path path;
		Supplier<InputStream> stream;
		String filename;
		String contentType;

	}

	class PartsIterator implements Iterator<byte[]>
	{

		private byte[] next;
		private boolean done;
		private final Iterator<Part> iter;
		private InputStream currentFileInput;

		PartsIterator()
		{
			iter = parts.iterator();
		}

		@Override
		public boolean hasNext()
		{
			if (done)
				return false;
			if (next != null)
				return true;
			try
			{
				next = computeNext();
			} catch (IOException e)
			{
				throw new UncheckedIOException(e);
			}
			if (next == null)
			{
				done = true;
				return false;
			}
			return true;
		}

		@Override
		public byte[] next()
		{
			if (!hasNext())
				throw new NoSuchElementException();
			byte[] res = next;
			next = null;
			return res;
		}

		private byte[] computeNext() throws IOException
		{
			if (currentFileInput == null)
			{
				if (!iter.hasNext())
					return null;
				Part nextPart = iter.next();
				if (Part.TYPE.STRING.equals(nextPart.type))
				{
					String part
						= "--" + boundary + "\r\n"
						+ "Content-Disposition: form-data; name=" + nextPart.name + "\r\n"
						+ "Content-Type: text/plain; charset=UTF-8\r\n\r\n"
						+ nextPart.value + "\r\n";
					return part.getBytes(StandardCharsets.UTF_8);
				}
				if (Part.TYPE.FINAL_BOUNDARY.equals(nextPart.type))
				{
					return nextPart.value.getBytes(StandardCharsets.UTF_8);
				}
				String filename;
				String contentType;
				if (Part.TYPE.FILE.equals(nextPart.type))
				{
					Path path = nextPart.path;
					filename = path.getFileName().toString();
					contentType = Files.probeContentType(path);
					if (contentType == null)
						contentType = "application/octet-stream";
					currentFileInput = Files.newInputStream(path);
				} else
				{
					filename = nextPart.filename;
					contentType = nextPart.contentType;
					if (contentType == null)
						contentType = "application/octet-stream";
					currentFileInput = nextPart.stream.get();
				}
				String partHeader
					= "--" + boundary + "\r\n"
					+ "Content-Disposition: form-data; name=" + nextPart.name + "; filename=" + filename + "\r\n"
					+ "Content-Type: " + contentType + "\r\n\r\n";
				return partHeader.getBytes(StandardCharsets.UTF_8);
			} else
			{
				byte[] buf = new byte[8192];
				int r = currentFileInput.read(buf);
				if (r > 0)
				{
					byte[] actualBytes = new byte[r];
					System.arraycopy(buf, 0, actualBytes, 0, r);
					return actualBytes;
				} else
				{
					currentFileInput.close();
					currentFileInput = null;
					return "\r\n".getBytes(StandardCharsets.UTF_8);
				}
			}
		}
	}
}
