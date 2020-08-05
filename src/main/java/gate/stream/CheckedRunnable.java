package gate.stream;

@FunctionalInterface
public interface CheckedRunnable
{

	public void run() throws Exception;

}
