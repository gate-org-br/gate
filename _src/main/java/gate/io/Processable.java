package gate.io;

import java.io.IOException;

public interface Processable
{

	<T> void process(Processor<T> processor) throws IOException;

}
