
public interface IQueue<T> {
	void enq(T item) throws FullException;
	T deq() throws EmptyException;
}
