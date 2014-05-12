import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


public class SimpleHistogram<T>  implements IHistorgram<T>{
	ConcurrentHashMap<Long, Integer> histogram;
	ReentrantLock[] Lock;
	public SimpleHistogram() {
		histogram = new ConcurrentHashMap<Long, Integer>();
		
	}
	
	public SimpleHistogram(int size) {
		histogram = new ConcurrentHashMap<Long, Integer>();
		this.Lock = new ReentrantLock[2<<16];
		for (int i = 0 ; i < this.Lock.length;i++) {
			this.Lock[i] = new ReentrantLock();
		}
	}
	
	@Override
	public void add(T item) {
		try {
			this.Lock[((Long) item).intValue()].lock();
			if (!histogram.containsKey(item)) {
				histogram.put((Long)item, 0);
			}			
			histogram.put((Long)item, histogram.get((Long)item)+1);
		} finally {
			this.Lock[((Long) item).intValue()].unlock();
		}
	}

	@Override
	public void display() {
		int maxValue = 0;
		Long maxItem = 0L;
		for (Long item : histogram.keySet()) {
			System.out.println(String.format("Item %d Counter %d", item, histogram.get(item)));
			System.out.println(histogram.get(item));
			if (histogram.get(item) > maxValue) {
				maxValue = histogram.get(item);
				maxItem = item;
			}
		}
		System.out.println(String.format("Max Item %d Counter %d", maxItem, maxValue));
	}

}
