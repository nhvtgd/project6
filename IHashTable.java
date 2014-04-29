
public interface IHashTable<K,V> {
	boolean add(K key, V value);
	boolean remove(K key);
	boolean contains(K key);
	V get(K key);
	void display();
}
