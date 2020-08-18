package gate.io;

import gate.stream.CheckedPredicate;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface Processable
{

	<T> long process(Processor<T> processor) throws IOException, InvocationTargetException;

	default long processLines(CheckedPredicate<String> action) throws IOException, InvocationTargetException
	{
		return process(new LineProcessor(action));
	}

	default long processCSV(CheckedPredicate<List<String>> action) throws IOException, InvocationTargetException
	{
		return process(new CSVProcessor(action));
	}
}
