package gate.stream;

@FunctionalInterface
public interface CheckedRunnable
{

	void run() throws Exception;

}
