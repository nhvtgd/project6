
public interface IQueue<T> {
	void enq(T item);
	T deq() throws EmptyException;
}
