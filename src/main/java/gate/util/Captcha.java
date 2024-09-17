package gate.util;

import gate.annotation.Converter;
import gate.converter.custom.CaptchaConverter;
import gate.error.ConversionException;
import gate.io.Encryptor;
import gate.lang.json.JsonObject;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.imageio.ImageIO;

@Converter(CaptchaConverter.class)
public class Captcha
{

	private final String id;
	private final String text;
	private final long timestamp;
	private static final Encryptor ENCRYPTOR = Encryptor.of();
	private final static AtomicLong SEQUENCER = new AtomicLong();
	private static final Map<String, Captcha> INSTANCES = new ConcurrentHashMap<>();

	private Captcha(String id, String text, long timestamp)
	{
		this.id = id;
		this.text = text;
		this.timestamp = timestamp;
	}

	private static final int CAPTCHA_LENGTH = 6;
	private static final long CAPTCHA_EXPIRATION = 60 * 60000;
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static Captcha create()
	{
		INSTANCES.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue().timestamp > CAPTCHA_EXPIRATION);

		SecureRandom random = new SecureRandom();
		StringBuilder captcha = new StringBuilder(CAPTCHA_LENGTH);
		for (int i = 0; i < CAPTCHA_LENGTH; i++)
			captcha.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));

		String text = captcha.toString();
		long timestamp = System.currentTimeMillis();
		String id = UUID.randomUUID().toString() + SEQUENCER.getAndIncrement();
		Captcha instance = new Captcha(id, text, timestamp);
		INSTANCES.put(id, instance);
		return instance;
	}

	public static Captcha of(String cypher) throws ConversionException
	{
		INSTANCES.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue().timestamp > CAPTCHA_EXPIRATION);

		var json = JsonObject.parse(ENCRYPTOR.decrypt(cypher));

		return new Captcha(json.getString("id").orElseThrow(),
			json.getString("text").orElseThrow(),
			json.getLong("timestamp").orElseThrow());
	}

	public boolean validate(String text)
	{
		Captcha storedCaptcha = INSTANCES.get(id);

		if (storedCaptcha == null
			|| System.currentTimeMillis()
			- storedCaptcha.timestamp > CAPTCHA_EXPIRATION)
			return false;

		if (!storedCaptcha.text.equalsIgnoreCase(text))
			return false;

		INSTANCES.remove(id);
		return true;
	}

	@Override
	public String toString()
	{
		return ENCRYPTOR
			.encrypt(new JsonObject()
				.setString("id", id)
				.setString("text", text)
				.setLong("timestamp", timestamp)
				.toString());
	}

	public String image()
	{
		int width = 200;
		int height = 32;

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);

		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("Monospaced", Font.BOLD, 40));

		FontMetrics fontMetrics = g2d.getFontMetrics();
		int x = (width - fontMetrics.stringWidth(this.text)) / 2;
		int y = ((height - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();

		g2d.drawString(this.text, x, y);

		g2d.dispose();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try
		{
			ImageIO.write(bufferedImage, "png", outputStream);
			byte[] imageBytes = outputStream.toByteArray();

			String base64Image = Base64.getEncoder().encodeToString(imageBytes);
			return "data:image/png;base64," + base64Image;

		} catch (IOException e)
		{
			throw new UncheckedIOException("Erro ao converter a imagem para Data URL", e);
		}
	}
}
