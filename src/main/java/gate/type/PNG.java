package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.PNGConverter;
import gate.handler.PNGHandler;
import gate.type.collections.StringList;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;
import javax.imageio.ImageIO;

@Handler(PNGHandler.class)
@Converter(PNGConverter.class)
public class PNG implements Serializable
{

	private final int w;
	private final int h;
	private final byte[] bytes;
	private static final long serialVersionUID = 1L;

	public PNG(DataFile file)
	{
		this(file.getData());
	}

	public PNG(String string)
	{
		try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(string.split(",")[1])))
		{
			BufferedImage image = ImageIO.read(bais);
			w = image.getWidth();
			h = image.getHeight();
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				ImageIO.write(image, "png", baos);
				baos.flush();
				this.bytes = baos.toByteArray();
			}
		} catch (IOException e)
		{
			throw new IllegalArgumentException(String.format("The image type must be: %s", new StringList(ImageIO
				.getReaderFileSuffixes()).toString()));
		}
	}

	public PNG(byte[] bytes)
	{
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes))
		{
			BufferedImage image = ImageIO.read(bais);
			w = image.getWidth();
			h = image.getHeight();
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				ImageIO.write(image, "png", baos);
				baos.flush();
				this.bytes = baos.toByteArray();
			}
		} catch (IOException e)
		{
			throw new IllegalArgumentException(String.format("The image type must be: %s",
				new StringList(ImageIO.getReaderFileSuffixes()).toString()));
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
		return new DataFile(getBytes(), "png.png");
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
				return new PNG(baos.toByteArray());
			}
		} catch (IOException e)
		{
			throw new IllegalArgumentException("The image type must be: " + new StringList(ImageIO
				.getReaderFileSuffixes()));
		}
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
