import java.util.concurrent.atomic.AtomicInteger;


public class AtomicHistogram<T> implements IHistorgram<T>{
	AtomicInteger[] frequencyList;
	
	public AtomicHistogram(int size) {
		this.frequencyList = new AtomicInteger[2<<16];
		for (int i = 0 ; i < this.frequencyList.length;i++) {
			this.frequencyList[i] = new AtomicInteger(0);
		}
	}
	@Override
	public void add(T item) {
		this.frequencyList[((Long)item).intValue()].getAndIncrement();
		
	}

	@Override
	public void display() {
		int maxValue = 0;
		int maxItem = 0;
		for (int i = 0; i < this.frequencyList.length; i ++) {			
			if (this.frequencyList[i].get() > maxValue) {
				maxValue = this.frequencyList[i].get();
				maxItem = i;
			}
		}
		System.out.println(String.format("Max Item %d Counter %d", maxItem, maxValue));		
	}

}
