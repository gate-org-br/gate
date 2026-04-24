package gate.io;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StringReader implements Reader<String>
{

    private static final StringReader INSTANCE = new StringReader();
    private static final ConcurrentMap<String, StringReader> INSTANCES
            = new ConcurrentHashMap<String, StringReader>();

    private final String charset;

    private StringReader(String charset)
    {
        this.charset = charset;
    }

    private StringReader()
    {
        this(Charset.defaultCharset().name());
    }

    public static StringReader getInstance()
    {
        return INSTANCE;
    }

    public static StringReader getInstance(String charset)
    {
        return INSTANCES.computeIfAbsent(charset, StringReader::new);
    }

    @Override
    public String read(InputStream is)
    {
        try (StringWriter writer = new StringWriter())
        {
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(is, charset));

            for (int c = reader.read(); c != -1; c = reader.read())
                writer.write((char) c);
            writer.flush();
            return writer.toString();
        } catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public String getCharset()
    {
        return charset;
    }

    public static String read(File file)
    {
        try (FileInputStream is = new FileInputStream(file))
        {
            return INSTANCE.read(is);
        } catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
    }

    public static String read(java.io.Reader reader)
    {
        try
        {
            StringBuilder string = new StringBuilder();
            for (int c = reader.read(); c != -1; c = reader.read())
                string.append((char) c);
            return string.toString();
        } catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
    }

    public static String read(String charset, File file)
    {
        try (FileInputStream is = new FileInputStream(file))
        {
            return getInstance(charset).read(is);
        } catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
    }

    public static String read(java.net.URL url)
    {
        try (InputStream is = url.openStream())
        {
            return INSTANCE.read(is);
        } catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
    }

    public static String read(String charset, java.net.URL url)
    {
        try (InputStream is = url.openStream())
        {
            return INSTANCE.read(is);
        } catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
    }
}
