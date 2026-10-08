package gate.security.hash;

import java.io.Serializable;

public interface Hash extends Serializable
{

	public boolean verify(String password);
}
