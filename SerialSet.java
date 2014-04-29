import java.util.concurrent.ConcurrentSkipListSet;

public class SerialSet<K> implements IHashSet<K> {
	final ConcurrentSkipListSet<K> setWrapper;
	public SerialSet() {
		this.setWrapper = new ConcurrentSkipListSet<K>();
	}
	
	@Override
	public boolean add(K item) {
		return setWrapper.add(item);
	}
	
	@Override
	public boolean remove(K item) {
		return setWrapper.remove(item);		
	}
	
	@Override
	public boolean contains(K item) {		
		return setWrapper.contains(item);
	}
	
	@Override
	public void display() {
		for (Object item : setWrapper.toArray()) {
			System.out.println(item);
		}		
	}
	
	

}
