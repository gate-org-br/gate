package gate.util;

import gate.io.SSH;
import gate.io.LineReader;
import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

public class SSHTest
{

	@Test
	@Ignore
	public void test01() throws IOException
	{
		try (SSH ssh = SSH.connect("127.0.0.1", 22))
		{
			ssh.authenticate("davins", "passwd822424");
			ssh.call("ls").read(LineReader.getInstance())
				.forEach(System.out::println);

		}
	}
}
