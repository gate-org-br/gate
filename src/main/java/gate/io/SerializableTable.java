package gate.io;

import gate.annotation.SecurityKey;
import gate.error.AppError;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

class SerializableTable<T extends Serializable> extends AbstractTable<T>
{

	public SerializableTable(Class<T> type, File file, Set<T> values)
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
				if (type.isAnnotationPresent(SecurityKey.class))
				{
					Cipher cipher = Cipher.getInstance(ALGORITHM);
					String value = type.getAnnotation(SecurityKey.class).value();
					byte[] key = DatatypeConverter.parseBase64Binary(value);
					SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
					cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

					try (CipherOutputStream cipherOutputStream = new CipherOutputStream(deflaterOutputStream, cipher);
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(cipherOutputStream))
					{
						objectOutputStream.writeObject(values);
						objectOutputStream.flush();
					}
				} else
				{
					try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(deflaterOutputStream))
					{
						objectOutputStream.writeObject(values);
						objectOutputStream.flush();
					}
				}
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException ex)
			{
				throw new AppError(ex);
			}
		} else
			file.delete();
	}

	static <T extends Serializable> Table<T> create(Class<T> type, File file)
	{
		if (!file.exists())
			return new SerializableTable(type, file, new HashSet<>());

		try (FileInputStream fileInputStream = new FileInputStream(file);
			InflaterInputStream inflaterInputStream = new InflaterInputStream(fileInputStream))
		{
			if (type.isAnnotationPresent(SecurityKey.class))
			{
				Cipher cipher = Cipher.getInstance(ALGORITHM);
				String value = type.getAnnotation(SecurityKey.class).value();
				byte[] key = DatatypeConverter.parseBase64Binary(value);
				SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
				cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

				try (CipherInputStream cipherInputStream = new CipherInputStream(inflaterInputStream, cipher);
					ObjectInputStream objectInputStream = new ObjectInputStream(cipherInputStream))
				{
					Set<T> values = (Set<T>) objectInputStream.readObject();
					return new SerializableTable(type, file, values);
				}
			} else
			{
				try (ObjectInputStream objectInputStream = new ObjectInputStream(inflaterInputStream))
				{
					Set<T> values = (Set<T>) objectInputStream.readObject();
					return new SerializableTable(type, file, values);
				}
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | ClassNotFoundException | IOException ex)
		{
			throw new AppError(ex);
		}
	}
}
