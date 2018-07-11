package gate.io;

public interface Observable<T>
{

	public void addObserver(Observer<T> observer);

	public void remObserver(Observer<T> observer);
}
