package gate.type;

import gate.error.AppError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.text.ParseException;
import java.util.Base64;
import java.util.Enumeration;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AccessCode
{

	private final String value;
	private final String macAddress;
	private final DateInterval period;
	public static final String ALGORITHM = "RSA";

	public AccessCode(String value)
	{
		this.value = value;

		try (ObjectInputStream stream
			= new ObjectInputStream(getClass().getResourceAsStream("public.key")))
		{
			PublicKey publicKey = (PublicKey) stream.readObject();
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			String string = new String(cipher.doFinal(Base64.getDecoder().decode(value)), StandardCharsets.UTF_8);
			macAddress = string.substring(0, 13);
			period = new DateInterval(Date.of(string.substring(0, 21)), Date.of(string.substring(0, 21)));

		} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
			| IllegalBlockSizeException | BadPaddingException | ParseException e)
		{
			throw new AppError(e);
		}
	}

	public String getValue()
	{
		return value;
	}

	public String getMacAddress()
	{
		return macAddress;
	}

	public DateInterval getPeriod()
	{
		return period;
	}

	public boolean check(PublicKey publicLey, DateTimeInterval dateTimeInterval)
	{
		try
		{
			if (period.contains(Date.now()))
			{
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
				while (networkInterfaces.hasMoreElements())
				{
					NetworkInterface networkInterface = networkInterfaces.nextElement();
					byte[] mac = networkInterface.getHardwareAddress();
					if (mac != null)
					{
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < mac.length; i++)
							sb.append(String.format("%02X", mac[i]));
						if (sb.toString().equals(macAddress))
							return true;
					}
				}
			}

			return false;
		} catch (SocketException e)
		{
			throw new AppError(e);
		}
	}
}
