import java.util.concurrent.ConcurrentHashMap;

public class JavaHashMapWrapper<K, V> implements IHashTable<K, V> {
	ConcurrentHashMap<K,V> map;
	public JavaHashMapWrapper() {
		map = new ConcurrentHashMap<K, V>();
	}
	
	@Override
	public boolean add(K key, V value) {
		return map.put(key, value) != null;
	}

	@Override
	public boolean remove(K key) {
		return map.remove(key) != null;
	}

	@Override
	public boolean contains(K key) {
		return map.containsKey(key);
	}

	@Override
	public V get(K key) {
		return map.get(key);
	}
	@Override
	public void display() {
		for (K item: map.keySet()){
			System.out.println(String.format("Key %d", (Integer) item));
			((SerialSet<Integer>)map.get(item)).display();
		}
		
	}
	

}
