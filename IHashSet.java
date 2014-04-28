import java.util.Set;


public interface IHashSet<K> {
	boolean add(K item);
	boolean remove(K item);
	boolean contains(K item);
	void display();
}
