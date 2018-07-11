package gate.io;

import gate.annotation.SecurityKey;
import gate.converter.Converter;
import gate.error.AppError;
import gate.util.IO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

class JsonTable<T> extends AbstractTable<T>
{

	private JsonTable(Class<T> type, File file, Set<T> values)
	{
		super(type, file, values);
	}

	@Override
	protected void persist()
	{
		if (!values.isEmpty())
		{
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
			try (FileOutputStream fileOutputStream = new FileOutputStream(file);
				DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(fileOutputStream))
			{
				String json = Converter.toJson(values);
				if (type.isAnnotationPresent(SecurityKey.class))
				{
					Cipher cipher = Cipher.getInstance(ALGORITHM);
					String stringKey = type.getAnnotation(SecurityKey.class).value();
					byte[] key = DatatypeConverter.parseBase64Binary(stringKey);
					SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
					cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

					try (CipherOutputStream cipherOutputStream = new CipherOutputStream(deflaterOutputStream, cipher);
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(cipherOutputStream)))
					{
						writer.write(json);
						writer.flush();
					}
				} else
				{
					try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(deflaterOutputStream)))
					{
						writer.write(json);
						writer.flush();
					}
				}
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException ex)
			{
				throw new AppError(ex);
			}
		} else
			file.delete();
	}

	static <T> JsonTable<T> create(Class<T> type, File file)
	{
		if (!file.exists())
			return new JsonTable(type, file, new HashSet<>());

		try (FileInputStream fileInputStream = new FileInputStream(file);
			InflaterInputStream inflaterInputStream = new InflaterInputStream(fileInputStream))
		{
			if (type.isAnnotationPresent(SecurityKey.class))
			{
				Cipher cipher = Cipher.getInstance(ALGORITHM);
				String stringKey = type.getAnnotation(SecurityKey.class).value();
				byte[] key = DatatypeConverter.parseBase64Binary(stringKey);
				SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
				cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

				try (CipherInputStream cipherInputStream = new CipherInputStream(inflaterInputStream, cipher))
				{
					String json = IO.readString(cipherInputStream);
					Set<T> values = Converter.fromJson(Set.class, type, json);
					return new JsonTable(type, file, values != null ? values : new HashSet<>());
				}
			} else
			{
				String json = IO.readString(inflaterInputStream);
				Set<T> values = Converter.fromJson(Set.class, type, json);
				return new JsonTable(type, file, values != null ? values : new HashSet<>());
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException ex)
		{
			throw new AppError(ex);
		}
	}
}
