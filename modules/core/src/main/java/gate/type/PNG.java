package gate.type;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

public class PNG implements Serializable
{

	private final int w;
	private final int h;
	private final byte[] bytes;

	@Serial
	private static final long serialVersionUID = 1L;

	private PNG(int w, int h, byte[] bytes)
	{
		this.w = w;
		this.h = h;
		this.bytes = bytes;
	}

	public static PNG valueOf(DataFile file)
	{
		return PNG.valueOF(file.getData());
	}

	public static PNG valueOf(String string)
	{
		try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(string.split(",")[1])))
		{
			BufferedImage image = ImageIO.read(bais);
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				ImageIO.write(image, "png", baos);
				baos.flush();
				byte[] bytes = baos.toByteArray();

				return new PNG(image.getWidth(), image.getHeight(), bytes);
			}
		} catch (IOException e)
		{
			throw new IllegalArgumentException(String.format("The image type must be: %s", suffixes()));
		}
	}

	public static PNG valueOF(byte[] bytes)
	{
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes))
		{
			BufferedImage image = ImageIO.read(bais);
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				ImageIO.write(image, "png", baos);
				baos.flush();
				return new PNG(image.getWidth(), image.getHeight(), baos.toByteArray());
			}
		} catch (IOException e)
		{
			throw new IllegalArgumentException(String.format("The image type must be: %s", suffixes()));
		}
	}

	public byte[] getBytes()
	{
		return bytes;
	}

	public int getW()
	{
		return w;
	}

	public int getH()
	{
		return h;
	}

	public DataFile toDataFile()
	{
		return DataFile.of(getBytes(), "png.png");
	}

	@Override
	public String toString()
	{
		return String.format("data:image/png;base64,%s", Base64.getEncoder().encodeToString(bytes));
	}

	public PNG scale(int w, int h)
	{
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes))
		{
			BufferedImage image = ImageIO.read(bais);
			Image scaled = image.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
			image = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
			Graphics g = image.getGraphics();
			g.drawImage(scaled, 0, 0, null);
			g.dispose();

			try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				ImageIO.write(image, "png", baos);
				return new PNG(w, h, baos.toByteArray());
			}
		} catch (IOException e)
		{
			throw new IllegalArgumentException("The image type must be: " + suffixes());
		}
	}

	private static String suffixes()
	{
		return Arrays.stream(ImageIO.getReaderFileSuffixes()).collect(Collectors.joining("\n"));
	}

	public PNG wscale(int w)
	{
		return scale(w, (int) (w * ((double) this.h) / ((double) this.w)));
	}

	public PNG hscale(int h)
	{
		return scale((int) (h * ((double) this.w) / ((double) this.h)), h);
	}
}
