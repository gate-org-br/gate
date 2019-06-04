package gate.io;

public interface Observable<T>
{

	void addObserver(Observer<T> observer);

	void remObserver(Observer<T> observer);
}
