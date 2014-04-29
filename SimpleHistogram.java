import java.util.concurrent.ConcurrentHashMap;


public class SimpleHistogram<T>  implements IHistorgram<T>{
	ConcurrentHashMap<Long, Integer> histogram;
	
	public SimpleHistogram() {
		histogram = new ConcurrentHashMap<Long, Integer>();
	}
	
	@Override
	public void add(T item) {
		if (!histogram.containsKey(item)) {
			histogram.put((Long)item, 0);
		}
		int currentCount = histogram.get(item);
		histogram.put((Long)item, currentCount + 1);
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
