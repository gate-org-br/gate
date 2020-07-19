package gate.io;

import java.io.IOException;

public interface Processable
{

	<T> long process(Processor<T> processor) throws IOException;

}
